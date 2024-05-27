package com.qubacy.geoqq.data.mate.chat.repository.impl

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.qubacy.geoqq._common.util.livedata.extension.await
import com.qubacy.geoqq._common.util.livedata.extension.awaitUntilVersion
import com.qubacy.geoqq.data._common.repository._common.source.local.database.error._common.LocalErrorDatabaseDataSource
import com.qubacy.geoqq.data.mate.chat.model.DataMateChat
import com.qubacy.geoqq.data.mate.chat.model.toDataMateChat
import com.qubacy.geoqq.data.mate.chat.model.toMateChatLastMessageEntityPair
import com.qubacy.geoqq.data.mate.chat.repository._common.MateChatDataRepository
import com.qubacy.geoqq.data.mate.chat.repository._common.result.GetChatByIdDataResult
import com.qubacy.geoqq.data.mate.chat.repository._common.result.GetChatsDataResult
import com.qubacy.geoqq.data.mate.chat.repository._common.source.local.database._common.LocalMateChatDatabaseDataSource
import com.qubacy.geoqq.data.mate.chat.repository._common.source.remote.http.rest._common.api.response.GetChatResponse
import com.qubacy.geoqq.data.mate.chat.repository._common.source.remote.http.rest._common.api.response.GetChatsResponse
import com.qubacy.geoqq.data.mate.chat.repository._common.source.local.database._common.entity.MateChatEntity
import com.qubacy.geoqq.data.mate.chat.repository._common.source.remote.http.rest._common.RemoteMateChatHttpRestDataSource
import com.qubacy.geoqq.data.mate.message.repository._common.source.local.database._common.entity.MateMessageEntity
import com.qubacy.geoqq.data.user.model.DataUser
import com.qubacy.geoqq.data.user.repository._common.UserDataRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.coroutineContext

class MateChatDataRepositoryImpl @Inject constructor(
    coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO,
    coroutineScope: CoroutineScope = CoroutineScope(coroutineDispatcher),
    private val mErrorSource: LocalErrorDatabaseDataSource,
    private val mUserDataRepository: UserDataRepository,
    private val mLocalMateChatDatabaseDataSource: LocalMateChatDatabaseDataSource,
    private val mRemoteMateChatHttpRestDataSource: RemoteMateChatHttpRestDataSource
    // todo: add a websocket source;
) : MateChatDataRepository(coroutineDispatcher, coroutineScope) {
    companion object {
        const val TAG = "MateChatDataRepository"
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

                if (resolveGetChatsResult.isNewest) return@launch
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

        mLocalMateChatDatabaseDataSource.deleteChat(localChat)
        mRemoteMateChatHttpRestDataSource.deleteChat(chatId)
    }

    private fun deleteOverdueChats(
        loadedChatIds: List<Long>
    ) {
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
}