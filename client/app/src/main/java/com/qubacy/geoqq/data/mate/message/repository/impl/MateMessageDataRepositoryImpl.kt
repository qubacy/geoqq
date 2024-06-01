package com.qubacy.geoqq.data.mate.message.repository.impl

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.qubacy.geoqq._common.util.livedata.extension.await
import com.qubacy.geoqq._common.util.livedata.extension.awaitUntilVersion
import com.qubacy.geoqq.data._common.model.message.DataMessage
import com.qubacy.geoqq.data._common.model.message.toMateMessageEntity
import com.qubacy.geoqq.data._common.repository._common.result.DataResult
import com.qubacy.geoqq.data._common.repository._common.source.local.database.error._common.LocalErrorDatabaseDataSource
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.result.payload.WebSocketPayloadResult
import com.qubacy.geoqq.data._common.repository.message.result.ResolveMessagesDataResult
import com.qubacy.geoqq.data._common.repository.message.util.extension.resolveGetMessagesResponse
import com.qubacy.geoqq.data._common.repository.producing.source.ProducingDataSource
import com.qubacy.geoqq.data.mate.message.model.toDataMessage
import com.qubacy.geoqq.data.mate.message.repository._common.MateMessageDataRepository
import com.qubacy.geoqq.data.mate.message.repository._common.result.added.MateMessageAddedDataResult
import com.qubacy.geoqq.data.mate.message.repository._common.result.get.GetMessagesDataResult
import com.qubacy.geoqq.data.mate.message.repository._common.source.local.database._common.LocalMateMessageDatabaseDataSource
import com.qubacy.geoqq.data.mate.message.repository._common.source.local.database._common.entity.MateMessageEntity
import com.qubacy.geoqq.data.mate.message.repository._common.source.remote.http.rest._common.RemoteMateMessageHttpRestDataSource
import com.qubacy.geoqq.data.mate.message.repository._common.source.remote.http.websocket._common.RemoteMateMessageHttpWebSocketDataSource
import com.qubacy.geoqq.data.mate.message.repository._common.source.remote.http.websocket._common.event.payload.added.MateMessageAddedEventPayload
import com.qubacy.geoqq.data.mate.message.repository._common.source.remote.http.websocket._common.event.type.MateMessageEventType
import com.qubacy.geoqq.data.user.repository._common.UserDataRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.coroutines.coroutineContext

class MateMessageDataRepositoryImpl(
    coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO,
    coroutineScope: CoroutineScope = CoroutineScope(coroutineDispatcher),
    private val mErrorSource: LocalErrorDatabaseDataSource,
    private val mUserDataRepository: UserDataRepository,
    private val mLocalMateMessageDatabaseDataSource: LocalMateMessageDatabaseDataSource,
    private val mRemoteMateMessageHttpRestDataSource: RemoteMateMessageHttpRestDataSource,
    private val mRemoteMateMessageHttpWebSocketDataSource: RemoteMateMessageHttpWebSocketDataSource
) : MateMessageDataRepository(coroutineDispatcher, coroutineScope) {
    override val resultFlow: Flow<DataResult> = merge(
        mResultFlow,
        mRemoteMateMessageHttpWebSocketDataSource.eventFlow
            .mapNotNull { mapWebSocketResultToDataResult(it) }
    )

    override fun getProducingDataSources(): Array<ProducingDataSource> {
        return arrayOf(mRemoteMateMessageHttpWebSocketDataSource)
    }

    override suspend fun getMessages(
        chatId: Long,
        loadedMessageIds: List<Long>,
        offset: Int,
        count: Int
    ): LiveData<GetMessagesDataResult> {
        val resultLiveData = MutableLiveData<GetMessagesDataResult>()

        CoroutineScope(coroutineContext).launch {
            val localMessages = mLocalMateMessageDatabaseDataSource.getMessages(chatId, offset, count)
            val localDataMessagesResult = resolveMessageEntities(localMessages)
            val localDataMessages = localDataMessagesResult.messages

            if (localMessages.isNotEmpty())
                resultLiveData.postValue(
                    GetMessagesDataResult(false, offset, localDataMessages)
                )

            val getMessagesResponse = mRemoteMateMessageHttpRestDataSource.getMateMessages(chatId, offset, count)

            val resolveGetMessagesResponseResultLiveData =
                resolveGetMessagesResponse(mUserDataRepository, getMessagesResponse)

            var version = 0

            while (true) {
                val resolveGetMessagesResponseResult =
                    resolveGetMessagesResponseResultLiveData.awaitUntilVersion(version)
                val httpDataMessages = resolveGetMessagesResponseResult.messages

                ++version

                if (localDataMessages.size == httpDataMessages.size
                    && localDataMessages.containsAll(httpDataMessages)
                ) {
                    resultLiveData.postValue(
                        GetMessagesDataResult(
                        resolveGetMessagesResponseResult.isNewest)
                    )

                    if (resolveGetMessagesResponseResult.isNewest) return@launch startProducingUpdates()
                    else continue
                }

                resultLiveData.postValue(
                    GetMessagesDataResult(
                    resolveGetMessagesResponseResult.isNewest, offset, httpDataMessages)
                )

                if (localDataMessages.size - httpDataMessages.size > 0) {
                    val finalLoadedMessageIds = loadedMessageIds.plus(httpDataMessages.map { it.id })

                    deleteOverdueMessages(chatId, finalLoadedMessageIds)
                }

                val messagesToSave = httpDataMessages.map { it.toMateMessageEntity(chatId) }

                mLocalMateMessageDatabaseDataSource.saveMessages(messagesToSave)

                if (resolveGetMessagesResponseResult.isNewest) return@launch startProducingUpdates()
            }
        }

        return resultLiveData
    }

    override suspend fun sendMessage(chatId: Long, text: String) {
        // todo: implement using the websocket data source;

        // todo: delete:
        mRemoteMateMessageHttpRestDataSource.sendMateMessage(chatId, text)
    }

    private fun deleteOverdueMessages(
        chatId: Long,
        loadedMessageIds: List<Long>
    ) {
        if (loadedMessageIds.isEmpty()) mLocalMateMessageDatabaseDataSource.deleteAllMessages(chatId)
        else mLocalMateMessageDatabaseDataSource.deleteOtherMessagesByIds(chatId, loadedMessageIds)
    }

    /**
     * There's no need for this method to resolve messages with the newest data about users;
     */
    private suspend fun resolveMessageEntities(
        messageEntities: List<MateMessageEntity>
    ): ResolveMessagesDataResult {
        val userIds = messageEntities.map { it.userId }.toSet().toList()

        val resolveUsersResultLiveData = mUserDataRepository.resolveUsersWithLocalUser(userIds)
        val resolveUsersResult = resolveUsersResultLiveData.await()
        val userIdUserMap = resolveUsersResult.userIdUserMap

        val resolvedMessages = messageEntities.map {
            it.toDataMessage(userIdUserMap[it.userId]!!)
        }

        // todo: there's actually no need to return ResolveMessagesDataResult:
        return ResolveMessagesDataResult(
            resolveUsersResult.isNewest, resolvedMessages)
    }

    override fun processWebSocketPayloadResult(
        webSocketPayloadResult: WebSocketPayloadResult
    ): DataResult {
        return when (webSocketPayloadResult.type) {
            MateMessageEventType.MATE_MESSAGE_ADDED_EVENT_TYPE.title ->
                processMateMessageAddedEventPayload(
                    webSocketPayloadResult.payload as MateMessageAddedEventPayload)
            else -> throw IllegalArgumentException()
        }
    }

    private fun processMateMessageAddedEventPayload(
        payload: MateMessageAddedEventPayload
    ): DataResult {
        lateinit var dataMessage: DataMessage

        runBlocking {
            val getUserResult = mUserDataRepository.getUsersByIds(listOf(payload.userId)).await() // todo: alright?

            dataMessage = payload.toDataMessage(getUserResult.users.first())
        }

        val messageToSave = dataMessage.toMateMessageEntity(payload.chatId)

        mLocalMateMessageDatabaseDataSource.saveMessages(listOf(messageToSave))

        return MateMessageAddedDataResult(dataMessage)
    }
}