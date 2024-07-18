package com.qubacy.geoqq.data.mate.chat.repository.impl

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.qubacy.geoqq._common.util.livedata.extension.await
import com.qubacy.geoqq._common.util.livedata.extension.awaitUntilVersion
import com.qubacy.geoqq.data._common.repository._common.result.DataResult
import com.qubacy.geoqq.data._common.repository._common.source.local.database.error._common.LocalErrorDatabaseDataSource
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.result.payload.WebSocketPayloadResult
import com.qubacy.geoqq.data._common.repository.producing.source.ProducingDataSource
import com.qubacy.geoqq.data.mate.chat.model.DataMateChat
import com.qubacy.geoqq.data.mate.chat.model.toDataMateChat
import com.qubacy.geoqq.data.mate.chat.model.toMateChatLastMessageEntityPair
import com.qubacy.geoqq.data.mate.chat.repository._common.MateChatDataRepository
import com.qubacy.geoqq.data.mate.chat.repository._common.result.added.MateChatAddedDataResult
import com.qubacy.geoqq.data.mate.chat.repository._common.result.get.GetChatByIdDataResult
import com.qubacy.geoqq.data.mate.chat.repository._common.result.get.GetChatsDataResult
import com.qubacy.geoqq.data.mate.chat.repository._common.result.updated.MateChatUpdatedDataResult
import com.qubacy.geoqq.data.mate.chat.repository._common.source.local.database._common.LocalMateChatDatabaseDataSource
import com.qubacy.geoqq.data.mate.chat.repository._common.source.remote.http.rest._common.api.response.GetChatResponse
import com.qubacy.geoqq.data.mate.chat.repository._common.source.remote.http.rest._common.api.response.GetChatsResponse
import com.qubacy.geoqq.data.mate.chat.repository._common.source.local.database._common.entity.MateChatEntity
import com.qubacy.geoqq.data.mate.chat.repository._common.source.remote.http.rest._common.RemoteMateChatHttpRestDataSource
import com.qubacy.geoqq.data.mate.chat.repository._common.source.remote.http.websocket._common.RemoteMateChatHttpWebSocketDataSource
import com.qubacy.geoqq.data.mate.chat.repository._common.source.remote.http.websocket._common.event.payload.updated.MateChatEventPayload
import com.qubacy.geoqq.data.mate.chat.repository._common.source.remote.http.websocket._common.event.type.MateChatEventType
import com.qubacy.geoqq.data.mate.message.repository._common.source.local.database._common.entity.MateMessageEntity
import com.qubacy.geoqq.data.user.model.DataUser
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

class MateChatDataRepositoryImpl(
    coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO,
    coroutineScope: CoroutineScope = CoroutineScope(coroutineDispatcher),
    private val mErrorSource: LocalErrorDatabaseDataSource,
    private val mUserDataRepository: UserDataRepository,
    private val mLocalMateChatDatabaseDataSource: LocalMateChatDatabaseDataSource,
    private val mRemoteMateChatHttpRestDataSource: RemoteMateChatHttpRestDataSource,
    private val mRemoteMateChatHttpWebSocketDataSource: RemoteMateChatHttpWebSocketDataSource
) : MateChatDataRepository(coroutineDispatcher, coroutineScope) {
    companion object {
        const val TAG = "MateChatDataRepository"
    }

    override fun generateGeneralResultFlow(): Flow<DataResult> = merge(
        mResultFlow,
        mRemoteMateChatHttpWebSocketDataSource.eventFlow
            .mapNotNull { mapWebSocketResultToDataResult(it) }
    )

    override fun getProducingDataSources(): Array<ProducingDataSource> {
        return arrayOf(mRemoteMateChatHttpWebSocketDataSource)
    }

    override suspend fun getChats(
        loadedChatIds: List<Long>,
        offset: Int,
        count: Int
    ): LiveData<GetChatsDataResult> {
        val resultLiveData = MutableLiveData<GetChatsDataResult>()

        CoroutineScope(coroutineContext).launch {
            val localChats = mLocalMateChatDatabaseDataSource.getChats(offset, count)
            val localDataChats = resolveChatWithLastMessageMap(localChats)

            if (localChats.isNotEmpty())
                resultLiveData.postValue(GetChatsDataResult(false, offset, localDataChats))

            val getChatsResponse = mRemoteMateChatHttpRestDataSource.getChats(offset, count)

            val resolveGetChatsResultLiveData = resolveGetChatsResponse(offset, getChatsResponse)

            var version = 0

            while (true) {
                val resolveGetChatsResult = resolveGetChatsResultLiveData.awaitUntilVersion(version)
                val httpDataChats = resolveGetChatsResult.chats!!

                ++version

                if (localDataChats.size == httpDataChats.size
                    && localDataChats.containsAll(httpDataChats)
                ) {
                    resultLiveData.postValue(GetChatsDataResult(resolveGetChatsResult.isNewest))

                    if (resolveGetChatsResult.isNewest) return@launch
                    else continue
                }

                resultLiveData.postValue(
                    GetChatsDataResult(
                    resolveGetChatsResult.isNewest, offset, httpDataChats)
                )

                if (localDataChats.size - httpDataChats.size > 0) {
                    val finalLoadedChatIds = loadedChatIds.plus(httpDataChats.map { it.id })

                    deleteOverdueChats(finalLoadedChatIds)
                }

                val chatsToSave = httpDataChats.map { it.toMateChatLastMessageEntityPair() }

                mLocalMateChatDatabaseDataSource.saveChats(chatsToSave)

                if (resolveGetChatsResult.isNewest)
                    return@launch startProducingUpdates() // todo: ok?
            }
        }

        return resultLiveData
    }

    override suspend fun getChatById(chatId: Long): LiveData<GetChatByIdDataResult?> {
        val resultLiveData = MutableLiveData<GetChatByIdDataResult?>()

        CoroutineScope(coroutineContext).launch {
            val localChat = mLocalMateChatDatabaseDataSource.getChatById(chatId)
            val localDataChat = resolveChatWithLastMessageMap(localChat)
                .let { if (it.isNotEmpty()) it.first() else null }

            if (localDataChat != null)
                resultLiveData.postValue(GetChatByIdDataResult(false, localDataChat))

            val getChatResponse = mRemoteMateChatHttpRestDataSource.getChat(chatId)

            val resolveGetChatResultLiveData = resolveGetChatResponse(getChatResponse)

            var version = 0

            while (true) {
                val resolveGetChatResult = resolveGetChatResultLiveData.awaitUntilVersion(version)
                val httpDataChat = resolveGetChatResult.chat

                ++version

                if (localDataChat == httpDataChat) {
                    resultLiveData.postValue(null)

                    if (resolveGetChatResult.isNewest) return@launch
                    else continue
                }

                resultLiveData.postValue(
                    GetChatByIdDataResult(
                    resolveGetChatResult.isNewest, httpDataChat)
                )

                val chatToSave = httpDataChat.toMateChatLastMessageEntityPair()

                mLocalMateChatDatabaseDataSource.saveChats(listOf(chatToSave))

                if (resolveGetChatResult.isNewest) return@launch
            }
        }

        return resultLiveData
    }

    override suspend fun deleteChat(chatId: Long) {
        val localChat = mLocalMateChatDatabaseDataSource.getChatById(chatId).keys.first()

        mRemoteMateChatHttpRestDataSource.deleteChat(chatId)
        mLocalMateChatDatabaseDataSource.deleteChat(localChat)
    }

    private fun deleteOverdueChats(
        loadedChatIds: List<Long>
    ) {
        Log.d(TAG, "deleteOverdueChats(): loadedChatIds = $loadedChatIds;")

        if (loadedChatIds.isEmpty()) mLocalMateChatDatabaseDataSource.deleteAllChats()
        else mLocalMateChatDatabaseDataSource.deleteOtherChatsByIds(loadedChatIds)
    }

    /**
     * There's no need to await for the newest user data;
     */
    private suspend fun resolveChatWithLastMessageMap(
        chatWithLastMessageMap: Map<MateChatEntity, MateMessageEntity?>
    ): List<DataMateChat> {
        if (chatWithLastMessageMap.isEmpty()) return emptyList()

        val userIds = chatWithLastMessageMap.flatMap {
            mutableListOf(it.key.userId).also { _ -> it.value?.userId ?: return@also }
        }.toSet().toList()
        val resolveUsersResultLiveData = mUserDataRepository.resolveUsersWithLocalUser(userIds)
        val resolveUsersResult = resolveUsersResultLiveData.await()
        val userIdUserMap = resolveUsersResult.userIdUserMap

        return chatWithLastMessageMap.mapNotNull {
            val chatUser = userIdUserMap[it.key.userId] ?: return@mapNotNull null
            val lastMessageUser = it.value?.let { lastMessage -> userIdUserMap[lastMessage.userId] }

            it.toDataMateChat(chatUser, lastMessageUser)
        }
    }

    private suspend fun resolveGetChatsResponse(
        offset: Int,
        getChatsResponse: GetChatsResponse
    ): LiveData<GetChatsDataResult> {
        val resultLiveData = MutableLiveData<GetChatsDataResult>()

        if (getChatsResponse.chats.isEmpty()) {
            resultLiveData.postValue(GetChatsDataResult(true, offset, emptyList()))

            return resultLiveData
        }

        val userIds = getChatsResponse.chats.flatMap { chat ->
            mutableListOf(chat.userId).apply {
                chat.lastMessage?.also { add(it.userId) } ?: return@apply
            }
        }.toSet().toList()
        val resolveUsersResultLiveData = mUserDataRepository.resolveUsersWithLocalUser(userIds)

        CoroutineScope(coroutineContext).launch {
            var version = 0

            while (true) {
                val resolveUsersResult = resolveUsersResultLiveData.awaitUntilVersion(version)
                val userIdUserMap = resolveUsersResult.userIdUserMap

                ++version

                val chats = getChatsResponse.chats.map {
                    mapGetChatResponseToDataMateChat(it, userIdUserMap)
                }

                resultLiveData.postValue(
                    GetChatsDataResult(
                    resolveUsersResult.isNewest, offset, chats)
                )

                if (resolveUsersResult.isNewest) return@launch
            }
        }

        return resultLiveData
    }

    private suspend fun resolveGetChatResponse(
        getChatResponse: GetChatResponse
    ): LiveData<GetChatByIdDataResult> {
        val resultLiveData = MutableLiveData<GetChatByIdDataResult>()

        val userId = getChatResponse.userId
        val resolveUsersResultLiveData =
            mUserDataRepository.resolveUsersWithLocalUser(listOf(userId))

        CoroutineScope(coroutineContext).launch {
            var version = 0

            while (true) {
                val resolveUsersResult = resolveUsersResultLiveData.awaitUntilVersion(version)
                val userIdUserMap = resolveUsersResult.userIdUserMap

                ++version

                val chat = mapGetChatResponseToDataMateChat(getChatResponse, userIdUserMap)

                resultLiveData.postValue(GetChatByIdDataResult(resolveUsersResult.isNewest, chat))

                if (resolveUsersResult.isNewest) return@launch
            }
        }

        return resultLiveData
    }

    private fun mapGetChatResponseToDataMateChat(
        getChatResponse: GetChatResponse,
        users: Map<Long, DataUser>
    ): DataMateChat {
        return getChatResponse.let {
            val chatUser = users[it.userId]!!
            val lastMessageUser = it.lastMessage?.let { lastMessage -> users[lastMessage.userId] }

            it.toDataMateChat(chatUser, lastMessageUser)
        }
    }

    override fun processWebSocketPayloadResult(
        webSocketPayloadResult: WebSocketPayloadResult
    ): DataResult {
        return when (webSocketPayloadResult.type) {
            MateChatEventType.MATE_CHAT_ADDED_EVENT_TYPE_NAME.title ->
                processMateChatAddedEventPayload(
                    webSocketPayloadResult.payload as MateChatEventPayload)
            MateChatEventType.MATE_CHAT_UPDATED_EVENT_TYPE_NAME.title ->
                processMateChatUpdatedEventPayload(
                    webSocketPayloadResult.payload as MateChatEventPayload)
            else -> throw IllegalArgumentException()
        }
    }

    private fun processMateChatAddedEventPayload(
        payload: MateChatEventPayload
    ): DataResult {
        val dataMateChat = processMateChatEventPayload(payload)

        return MateChatAddedDataResult(dataMateChat)
    }

    private fun processMateChatUpdatedEventPayload(
        payload: MateChatEventPayload
    ): DataResult {
        val dataMateChat = processMateChatEventPayload(payload)

        return MateChatUpdatedDataResult(dataMateChat)
    }

    private fun processMateChatEventPayload(
        payload: MateChatEventPayload
    ): DataMateChat {
        lateinit var dataMateChat: DataMateChat

        runBlocking {
            dataMateChat = resolveMateChatEventPayload(payload)
        }

        val chatToSave = dataMateChat.toMateChatLastMessageEntityPair()

        mLocalMateChatDatabaseDataSource.saveChats(listOf(chatToSave))

        return dataMateChat
    }

    private suspend fun resolveMateChatEventPayload(
        payload: MateChatEventPayload
    ): DataMateChat {
        val userId = payload.userId
        val lastMessageUserId = payload.lastMessage?.userId
        val userIds = mutableListOf(userId).apply {
            if (lastMessageUserId != null) add(lastMessageUserId)
        }.toSet().toList()

        val resolveUsersResult = mUserDataRepository.resolveUsers(userIds).await() // todo: alright?
        val userIdUserMap = resolveUsersResult.userIdUserMap

        val user = userIdUserMap[userId]!!
        val lastMessageUser = lastMessageUserId?.let { userIdUserMap[it]!! }

        return payload.toDataMateChat(user, lastMessageUser)
    }
}