package com.qubacy.geoqq.data.mate.message.repository.impl

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.qubacy.geoqq._common.util.livedata.extension.await
import com.qubacy.geoqq._common.util.livedata.extension.awaitUntilVersion
import com.qubacy.geoqq.data._common.model.message.toMateMessageEntity
import com.qubacy.geoqq.data._common.repository._common.source.local.database.error._common.LocalErrorDatabaseDataSource
import com.qubacy.geoqq.data._common.repository.message.result.ResolveMessagesDataResult
import com.qubacy.geoqq.data._common.repository.message.util.extension.resolveGetMessagesResponse
import com.qubacy.geoqq.data.mate.message.model.toDataMessage
import com.qubacy.geoqq.data.mate.message.repository._common.MateMessageDataRepository
import com.qubacy.geoqq.data.mate.message.repository._common.result.GetMessagesDataResult
import com.qubacy.geoqq.data.mate.message.repository._common.source.local.database._common.LocalMateMessageDatabaseDataSource
import com.qubacy.geoqq.data.mate.message.repository._common.source.local.database._common.entity.MateMessageEntity
import com.qubacy.geoqq.data.mate.message.repository._common.source.remote.http.rest._common.RemoteMateMessageHttpRestDataSource
import com.qubacy.geoqq.data.user.repository._common.UserDataRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.coroutineContext

class MateMessageDataRepositoryImpl @Inject constructor(
    coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO,
    coroutineScope: CoroutineScope = CoroutineScope(coroutineDispatcher),
    private val mErrorSource: LocalErrorDatabaseDataSource,
    private val mUserDataRepository: UserDataRepository,
    private val mLocalMateMessageDatabaseDataSource: LocalMateMessageDatabaseDataSource,
    private val mRemoteMateMessageHttpRestDataSource: RemoteMateMessageHttpRestDataSource
    // todo: provide a websocket data source;
) : MateMessageDataRepository(coroutineDispatcher, coroutineScope) {
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

                    if (resolveGetMessagesResponseResult.isNewest) return@launch
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

                if (resolveGetMessagesResponseResult.isNewest) return@launch
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
}