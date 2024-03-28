package com.qubacy.geoqq.data.mate.chat.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.qubacy.geoqq.data._common.repository.producing.ProducingDataRepository
import com.qubacy.geoqq.data._common.util.http.executor.executeNetworkRequest
import com.qubacy.geoqq.data.error.repository.ErrorDataRepository
import com.qubacy.geoqq.data.mate.chat.model.DataMateChat
import com.qubacy.geoqq.data.mate.chat.model.toDataMateChat
import com.qubacy.geoqq.data.mate.chat.model.toMateChatLastMessageEntityPair
import com.qubacy.geoqq.data.mate.chat.repository.result.GetChatsDataResult
import com.qubacy.geoqq.data.mate.chat.repository.source.http.HttpMateChatDataSource
import com.qubacy.geoqq.data.mate.chat.repository.source.http.response.GetChatsResponse
import com.qubacy.geoqq.data.mate.chat.repository.source.local.LocalMateChatDataSource
import com.qubacy.geoqq.data.mate.chat.repository.source.local.entity.MateChatEntity
import com.qubacy.geoqq.data.mate.message.repository.source.local.entity.MateMessageEntity
import com.qubacy.geoqq.data.token.repository.TokenDataRepository
import com.qubacy.geoqq.data.user.repository.UserDataRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.coroutineContext

class MateChatDataRepository @Inject constructor(
    coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO,
    coroutineScope: CoroutineScope = CoroutineScope(coroutineDispatcher),
    private val mErrorDataRepository: ErrorDataRepository,
    private val mTokenDataRepository: TokenDataRepository,
    private val mUserDataRepository: UserDataRepository,
    private val mLocalMateChatDataSource: LocalMateChatDataSource,
    private val mHttpMateChatDataSource: HttpMateChatDataSource,
    // todo: add a websocket source;
) : ProducingDataRepository(coroutineDispatcher, coroutineScope) {
    suspend fun getChats(offset: Int, count: Int): LiveData<GetChatsDataResult> {
        val resultLiveData = MutableLiveData<GetChatsDataResult>()

        CoroutineScope(coroutineContext).launch {
            val localChats = mLocalMateChatDataSource.getChats(offset, count)
            val localDataChats = resolveChatWithLastMessageMap(localChats)

            if (localChats.isNotEmpty())
                resultLiveData.value = GetChatsDataResult(localDataChats)

            val accessToken = mTokenDataRepository.getTokens().accessToken
            val getChatsCall = mHttpMateChatDataSource.getChats(offset, count, accessToken)
            val getChatsResponse = executeNetworkRequest(mErrorDataRepository, getChatsCall)

            val httpDataChats = resolveGetChatsResponse(getChatsResponse)

            if (localDataChats.containsAll(httpDataChats)) return@launch

            if (localDataChats.isNotEmpty()) mResultFlow.emit(GetChatsDataResult(httpDataChats))
            else resultLiveData.value = GetChatsDataResult(httpDataChats)

            val chatsToSave = httpDataChats.map { it.toMateChatLastMessageEntityPair() }

            mLocalMateChatDataSource.saveChats(chatsToSave)
        }

        return resultLiveData
    }

    private suspend fun resolveChatWithLastMessageMap(
        chatWithLastMessageMap: Map<MateChatEntity, MateMessageEntity?>
    ): List<DataMateChat> {
        val userIds = chatWithLastMessageMap.flatMap {
            mutableListOf(it.key.userId).also { _ -> it.value?.userId ?: return@also }
        }.toSet().toList()
        val users = mUserDataRepository.resolveUsers(userIds)

        return chatWithLastMessageMap.map {
            val chatUser = users[it.key.userId]!!
            val lastMessageUser = it.value?.let { lastMessage -> users[lastMessage.userId] }

            it.toDataMateChat(chatUser, lastMessageUser)
        }
    }

    private suspend fun resolveGetChatsResponse(
        getChatsResponse: GetChatsResponse
    ): List<DataMateChat> {
        val userIds = getChatsResponse.chats.flatMap {
            mutableListOf(it.userId).also { _ -> it.lastMessage?.userId ?: return@also }
        }.toSet().toList()
        val users = mUserDataRepository.resolveUsers(userIds)

        return getChatsResponse.chats.map {
            val chatUser = users[it.userId]!!
            val lastMessageUser = it.lastMessage?.let { lastMessage -> users[lastMessage.userId] }

            it.toDataMateChat(chatUser, lastMessageUser)
        }
    }
}