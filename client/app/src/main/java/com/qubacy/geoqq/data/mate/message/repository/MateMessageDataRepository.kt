package com.qubacy.geoqq.data.mate.message.repository

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
    private var mUserDataRepository: UserDataRepository,
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

            if (localDataMessages.containsAll(httpDataMessages)) return@launch

            if (localDataMessages.isNotEmpty())
                mResultFlow.emit(GetMessagesDataResult(offset, httpDataMessages))
            else resultLiveData.postValue(GetMessagesDataResult(offset, httpDataMessages))

            if (localDataMessages.size - httpDataMessages.size > 0)
                deleteOverdueMessages(localDataMessages, httpDataMessages)

            val messagesToSave = httpDataMessages.map { it.toMateMessageEntity(chatId) }

            mLocalMateMessageDataSource.saveMessages(messagesToSave)
        }

        return resultLiveData
    }

    private fun deleteOverdueMessages(
        localDataMessages: List<DataMessage>,
        httpDataMessages: List<DataMessage>
    ) {
        val messagesToDelete = localDataMessages.filter { localMessage ->
            httpDataMessages.find { httpMessage -> httpMessage.id == localMessage.id } == null
        }

        mLocalMateMessageDataSource.deleteMessagesByIds(messagesToDelete.map { it.id })
    }

    suspend fun sendMessage(chatId: Long, message: DataMessage) {
        // todo: implement using the websocket data source;


    }

    private suspend fun resolveMessageEntities(
        messageEntities: List<MateMessageEntity>
    ): List<DataMessage> {
        val userIds = messageEntities.map { it.userId }.toSet().toList()
        val users = mUserDataRepository.resolveUsersWithLocalUser(userIds)

        return messageEntities.map { it.toDataMessage(users[it.userId]!!) }
    }

    private suspend fun resolveGetMessagesResponse(
        getMessagesResponse: GetMessagesResponse
    ): List<DataMessage> {
        val userIds = getMessagesResponse.messages.map { it.userId }.toSet().toList()
        val users = mUserDataRepository.resolveUsersWithLocalUser(userIds)

        return getMessagesResponse.messages.map { it.toDataMessage(users[it.userId]!!) }
    }
}