package com.qubacy.geoqq.data.mate.message.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.qubacy.geoqq.data._common.model.message.DataMessage
import com.qubacy.geoqq.data._common.model.message.toMateMessageEntity
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.executor.HttpCallExecutor
import com.qubacy.geoqq.data._common.repository.message.MessageDataRepository
import com.qubacy.geoqq.data._common.repository.message.util.extension.resolveGetMessagesResponse
import com.qubacy.geoqq.data._common.repository.producing.ProducingDataRepository
import com.qubacy.geoqq.data.error.repository.ErrorDataRepository
import com.qubacy.geoqq.data.mate.message.model.toDataMessage
import com.qubacy.geoqq.data.mate.message.repository.result.GetMessagesDataResult
import com.qubacy.geoqq.data.mate.message.repository.source.http.HttpMateMessageDataSource
import com.qubacy.geoqq.data.mate.message.repository.source.http.request.SendMateMessageRequest
import com.qubacy.geoqq.data.mate.message.repository.source.local.LocalMateMessageDataSource
import com.qubacy.geoqq.data.mate.message.repository.source.local.entity.MateMessageEntity
import com.qubacy.geoqq.data.token.repository.TokenDataRepository
import com.qubacy.geoqq.data.user.repository.UserDataRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.coroutineContext

class MateMessageDataRepository @Inject constructor(
    coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO,
    coroutineScope: CoroutineScope = CoroutineScope(coroutineDispatcher),
    private val mErrorDataRepository: ErrorDataRepository,
    private val mTokenDataRepository: TokenDataRepository,
    private val mUserDataRepository: UserDataRepository,
    private val mLocalMateMessageDataSource: LocalMateMessageDataSource,
    private val mHttpMateMessageDataSource: HttpMateMessageDataSource,
    private val mHttpCallExecutor: HttpCallExecutor
    // todo: provide a websocket data source;
) : ProducingDataRepository(coroutineDispatcher, coroutineScope), MessageDataRepository {
    suspend fun getMessages(
        chatId: Long,
        loadedMessageIds: List<Long>,
        offset: Int,
        count: Int
    ): LiveData<GetMessagesDataResult?> {
        val resultLiveData = MutableLiveData<GetMessagesDataResult?>()

        CoroutineScope(coroutineContext).launch {
            val localMessages = mLocalMateMessageDataSource.getMessages(chatId, offset, count)
            val localDataMessages = resolveMessageEntities(localMessages)

            if (localMessages.isNotEmpty())
                resultLiveData.postValue(GetMessagesDataResult(offset, localDataMessages))

            val accessToken = mTokenDataRepository.getTokens().accessToken

            val getMessagesCall = mHttpMateMessageDataSource
                .getMateMessages(chatId, offset, count, accessToken)
            val getMessagesResponse = mHttpCallExecutor.executeNetworkRequest(getMessagesCall)

            val httpDataMessages = resolveGetMessagesResponse(mUserDataRepository, getMessagesResponse)

            if (localDataMessages.size == httpDataMessages.size
                && localDataMessages.containsAll(httpDataMessages)
            ) {
                if (localDataMessages.isEmpty()) resultLiveData.postValue(null)
                else return@launch
            }

            if (localDataMessages.isNotEmpty())
                mResultFlow.emit(GetMessagesDataResult(offset, httpDataMessages))
            else resultLiveData.postValue(GetMessagesDataResult(offset, httpDataMessages))

            //Log.d("TEST", "localDataMessages.size = ${localDataMessages.size}; httpDataMessages.size = ${httpDataMessages.size};")

            if (localDataMessages.size - httpDataMessages.size > 0) {
                val finalLoadedMessageIds = loadedMessageIds.plus(httpDataMessages.map { it.id })

                deleteOverdueMessages(chatId, finalLoadedMessageIds)
            }

            val messagesToSave = httpDataMessages.map { it.toMateMessageEntity(chatId) }

            mLocalMateMessageDataSource.saveMessages(messagesToSave)
        }

        return resultLiveData
    }

    private fun deleteOverdueMessages(
        chatId: Long,
        loadedMessageIds: List<Long>
    ) {
        //Log.d("TEST", "deleteOverdueMessages(): chatId = $chatId; loadedMessageIds: $loadedMessageIds;")

        if (loadedMessageIds.isEmpty()) mLocalMateMessageDataSource.deleteAllMessages(chatId)
        else mLocalMateMessageDataSource.deleteOtherMessagesByIds(chatId, loadedMessageIds)
    }

    suspend fun sendMessage(chatId: Long, text: String) {
        // todo: implement using the websocket data source;

        // todo: delete:
        val accessToken = mTokenDataRepository.getTokens().accessToken

        val sendMessageRequest = SendMateMessageRequest(accessToken, text)
        val sendMessageCall = mHttpMateMessageDataSource.sendMateMessage(chatId, sendMessageRequest)

        mHttpCallExecutor.executeNetworkRequest(sendMessageCall)
    }

    private suspend fun resolveMessageEntities(
        messageEntities: List<MateMessageEntity>
    ): List<DataMessage> {
        val userIds = messageEntities.map { it.userId }.toSet().toList()
        val users = mUserDataRepository.resolveUsersWithLocalUser(userIds)

        return messageEntities.mapNotNull {
            val user = users[it.userId] ?: return@mapNotNull null // todo: think of this;

            it.toDataMessage(user)
        }
    }
}