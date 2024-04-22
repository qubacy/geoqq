package com.qubacy.geoqq.data.mate.message.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.qubacy.geoqq.data._common.model.message.DataMessage
import com.qubacy.geoqq.data._common.model.message.toDataMessage
import com.qubacy.geoqq.data._common.model.message.toMateMessageEntity
import com.qubacy.geoqq.data._common.repository._common.source.http._common.response.message.GetMessagesResponse
import com.qubacy.geoqq.data._common.repository.producing.ProducingDataRepository
import com.qubacy.geoqq.data._common.util.http.executor.executeNetworkRequest
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
import okhttp3.OkHttpClient
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
    private val mHttpClient: OkHttpClient
    // todo: provide a websocket data source;
) : ProducingDataRepository(coroutineDispatcher, coroutineScope) {
    suspend fun getMessages(chatId: Long, offset: Int, count: Int): LiveData<GetMessagesDataResult?> {
        val resultLiveData = MutableLiveData<GetMessagesDataResult?>()

        CoroutineScope(coroutineContext).launch {
            val localMessages = mLocalMateMessageDataSource.getMessages(chatId, offset, count)
            val localDataMessages = resolveMessageEntities(localMessages)

            if (localMessages.isNotEmpty())
                resultLiveData.postValue(GetMessagesDataResult(offset, localDataMessages))

            val accessToken = mTokenDataRepository.getTokens().accessToken

            val getMessagesCall = mHttpMateMessageDataSource
                .getMateMessages(chatId, offset, count, accessToken)
            val getMessagesResponse = executeNetworkRequest(
                mErrorDataRepository, mHttpClient, getMessagesCall)

            if (getMessagesResponse.messages.isEmpty()) {
                if (localDataMessages.isNotEmpty()) return@launch
                else resultLiveData.postValue(null)
            }

            val httpDataMessages = resolveGetMessagesResponse(getMessagesResponse)

            Log.d("TEST", "getMessages(): httpDataMessages = ${httpDataMessages.map { it.toString() }}")

            if (localDataMessages.containsAll(httpDataMessages)
                && localDataMessages.size == httpDataMessages.size
            ) {
                return@launch
            }

            if (localDataMessages.isNotEmpty())
                mResultFlow.emit(GetMessagesDataResult(offset, httpDataMessages))
            else resultLiveData.postValue(GetMessagesDataResult(offset, httpDataMessages))

            Log.d("TEST", "localDataMessages.size = ${localDataMessages.size}; httpDataMessages.size = ${httpDataMessages.size};")

            if (localDataMessages.size - httpDataMessages.size > 0)
                deleteOverdueMessages(chatId, localDataMessages, httpDataMessages)

            val messagesToSave = httpDataMessages.map { it.toMateMessageEntity(chatId) }

            mLocalMateMessageDataSource.saveMessages(messagesToSave)
        }

        return resultLiveData
    }

    private fun deleteOverdueMessages(
        chatId: Long,
        localDataMessages: List<DataMessage>,
        httpDataMessages: List<DataMessage>
    ) {
        val messagesToDelete = localDataMessages.filter { localMessage ->
            httpDataMessages.find { httpMessage -> httpMessage.id == localMessage.id } == null
        }

        Log.d("TEST", "deleteOverdueMessages(): startId = ${messagesToDelete.first().id}; endId = ${messagesToDelete.last().id};")

        mLocalMateMessageDataSource.deleteMessagesByIds(chatId, messagesToDelete.map { it.id })
    }

    suspend fun sendMessage(chatId: Long, text: String) {
        // todo: implement using the websocket data source;

        // todo: delete:
        val accessToken = mTokenDataRepository.getTokens().accessToken

        val sendMessageRequest = SendMateMessageRequest(accessToken, text)
        val sendMessageCall = mHttpMateMessageDataSource.sendMateMessage(chatId, sendMessageRequest)

        executeNetworkRequest(mErrorDataRepository, mHttpClient, sendMessageCall)
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

    private suspend fun resolveGetMessagesResponse(
        getMessagesResponse: GetMessagesResponse
    ): List<DataMessage> {
        val userIds = getMessagesResponse.messages.map { it.userId }.toSet().toList()
        val users = mUserDataRepository.resolveUsersWithLocalUser(userIds)

        return getMessagesResponse.messages.map { it.toDataMessage(users[it.userId]!!) }
    }
}