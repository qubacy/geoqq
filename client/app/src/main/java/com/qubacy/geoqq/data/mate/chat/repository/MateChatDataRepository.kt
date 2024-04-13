package com.qubacy.geoqq.data.mate.chat.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.qubacy.geoqq.data._common.repository.producing.ProducingDataRepository
import com.qubacy.geoqq.data._common.util.http.executor.executeNetworkRequest
import com.qubacy.geoqq.data.error.repository.ErrorDataRepository
import com.qubacy.geoqq.data.mate.chat.model.DataMateChat
import com.qubacy.geoqq.data.mate.chat.model.toDataMateChat
import com.qubacy.geoqq.data.mate.chat.model.toMateChatLastMessageEntityPair
import com.qubacy.geoqq.data.mate.chat.repository.result.GetChatByIdDataResult
import com.qubacy.geoqq.data.mate.chat.repository.result.GetChatsDataResult
import com.qubacy.geoqq.data.mate.chat.repository.source.http.HttpMateChatDataSource
import com.qubacy.geoqq.data.mate.chat.repository.source.http.request.DeleteChatRequest
import com.qubacy.geoqq.data.mate.chat.repository.source.http.response.GetChatResponse
import com.qubacy.geoqq.data.mate.chat.repository.source.http.response.GetChatsResponse
import com.qubacy.geoqq.data.mate.chat.repository.source.local.LocalMateChatDataSource
import com.qubacy.geoqq.data.mate.chat.repository.source.local.entity.MateChatEntity
import com.qubacy.geoqq.data.mate.message.repository.source.local.entity.MateMessageEntity
import com.qubacy.geoqq.data.token.repository.TokenDataRepository
import com.qubacy.geoqq.data.user.model.DataUser
import com.qubacy.geoqq.data.user.repository.UserDataRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
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
    private val mHttpClient: OkHttpClient
    // todo: add a websocket source;
) : ProducingDataRepository(coroutineDispatcher, coroutineScope) {
    suspend fun getChats(offset: Int, count: Int): LiveData<GetChatsDataResult?> {
        val resultLiveData = MutableLiveData<GetChatsDataResult?>()

        CoroutineScope(coroutineContext).launch {
            val localChats = mLocalMateChatDataSource.getChats(offset, count)
            val localDataChats = resolveChatWithLastMessageMap(localChats)

            if (localChats.isNotEmpty())
                resultLiveData.postValue(GetChatsDataResult(offset, localDataChats))

            val accessToken = mTokenDataRepository.getTokens().accessToken
            val getChatsCall = mHttpMateChatDataSource.getChats(offset, count, accessToken)
            val getChatsResponse = executeNetworkRequest(
                mErrorDataRepository, mHttpClient, getChatsCall)

            if (getChatsResponse.chats.isEmpty()) {
                if (localDataChats.isNotEmpty()) return@launch
                else resultLiveData.postValue(null)
            }

            val httpDataChats = resolveGetChatsResponse(getChatsResponse)

            if (localDataChats.containsAll(httpDataChats)) return@launch

            if (localDataChats.isNotEmpty())
                mResultFlow.emit(GetChatsDataResult(offset, httpDataChats))
            else resultLiveData.postValue(GetChatsDataResult(offset, httpDataChats))

            if (localDataChats.size - httpDataChats.size > 0)
                deleteOverdueChats(localDataChats, httpDataChats)

            val chatsToSave = httpDataChats.map { it.toMateChatLastMessageEntityPair() }

            mLocalMateChatDataSource.saveChats(chatsToSave)
        }

        return resultLiveData
    }

    suspend fun getChatById(chatId: Long): LiveData<GetChatByIdDataResult> {
        val resultLiveData = MutableLiveData<GetChatByIdDataResult>()

        CoroutineScope(coroutineContext).launch {
            val localChat = mLocalMateChatDataSource.getChatById(chatId)
            val localDataChat = resolveChatWithLastMessageMap(localChat)
                .let { if (it.isNotEmpty()) it.first() else null }

            if (localDataChat != null)
                resultLiveData.postValue(GetChatByIdDataResult(localDataChat))

            val accessToken = mTokenDataRepository.getTokens().accessToken
            val getChatCall = mHttpMateChatDataSource.getChat(chatId, accessToken)
            val getChatResponse = executeNetworkRequest(
                mErrorDataRepository, mHttpClient, getChatCall)

            val httpDataChat = resolveGetChatResponse(getChatResponse)

            if (localDataChat == httpDataChat) return@launch

            if (localDataChat != null)
                mResultFlow.emit(GetChatByIdDataResult(httpDataChat))
            else resultLiveData.postValue(GetChatByIdDataResult(httpDataChat))

            val chatToSave = httpDataChat.toMateChatLastMessageEntityPair()

            mLocalMateChatDataSource.saveChats(listOf(chatToSave))
        }

        return resultLiveData
    }

    suspend fun deleteChat(chatId: Long) {
        val localChat = mLocalMateChatDataSource.getChatById(chatId).keys.first()

        mLocalMateChatDataSource.deleteChat(localChat)

        val accessToken = mTokenDataRepository.getTokens().accessToken

        val deleteChatRequest = DeleteChatRequest(accessToken)
        val deleteChatCall = mHttpMateChatDataSource.deleteChat(chatId, deleteChatRequest)

        executeNetworkRequest(mErrorDataRepository, mHttpClient, deleteChatCall)
    }

    private fun deleteOverdueChats(
        localDataChats: List<DataMateChat>,
        httpDataChats: List<DataMateChat>
    ) {
        val chatsToDelete = localDataChats.filter { localChat ->
            httpDataChats.find { httpChat -> httpChat.id == localChat.id } == null
        }

        mLocalMateChatDataSource.deleteChatsByIds(chatsToDelete.map { it.id })
    }

    private suspend fun resolveChatWithLastMessageMap(
        chatWithLastMessageMap: Map<MateChatEntity, MateMessageEntity?>
    ): List<DataMateChat> {
        val userIds = chatWithLastMessageMap.flatMap {
            mutableListOf(it.key.userId).also { _ -> it.value?.userId ?: return@also }
        }.toSet().toList()
        val users = mUserDataRepository.resolveUsersWithLocalUser(userIds)

        return chatWithLastMessageMap.map {
            val chatUser = users[it.key.userId]!!
            val lastMessageUser = it.value?.let { lastMessage -> users[lastMessage.userId] }

            it.toDataMateChat(chatUser, lastMessageUser)
        }
    }

    private suspend fun resolveGetChatsResponse(
        getChatsResponse: GetChatsResponse
    ): List<DataMateChat> {
        val userIds = getChatsResponse.chats.flatMap { chat ->
            mutableListOf(chat.userId).apply {
                chat.lastMessage?.also { add(it.userId) } ?: return@apply
            }
        }.toSet().toList()
        val users = mUserDataRepository.resolveUsersWithLocalUser(userIds)

        return getChatsResponse.chats.map {
            mapGetChatResponseToDataMateChat(it, users)
        }
    }

    private suspend fun resolveGetChatResponse(
        getChatResponse: GetChatResponse
    ): DataMateChat {
        val userId = getChatResponse.userId
        val users = mUserDataRepository.resolveUsersWithLocalUser(listOf(userId))

        return mapGetChatResponseToDataMateChat(getChatResponse, users)
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
}