package com.qubacy.geoqq.data.mate.chat.repository

import com.qubacy.geoqq.common.error.ErrorContext
import com.qubacy.geoqq.data.common.repository.common.result.common.Result
import com.qubacy.geoqq.data.common.repository.common.result.error.ErrorResult
import com.qubacy.geoqq.data.common.repository.common.result.interruption.InterruptionResult
import com.qubacy.geoqq.data.common.repository.common.source.network.model.response.common.Response
import com.qubacy.geoqq.data.common.repository.network.common.result.ExecuteNetworkRequestResult
import com.qubacy.geoqq.data.common.repository.network.updatable.UpdatableDataRepository
import com.qubacy.geoqq.data.common.repository.network.updatable.source.update.update.Update
import com.qubacy.geoqq.data.mate.chat.model.DataMateChat
import com.qubacy.geoqq.data.mate.chat.repository.result.GetChatsResult
import com.qubacy.geoqq.data.mate.chat.repository.result.GetChatsWithDatabaseResult
import com.qubacy.geoqq.data.mate.chat.repository.result.GetChatsWithNetworkResult
import com.qubacy.geoqq.data.mate.chat.repository.source.local.LocalMateChatDataSource
import com.qubacy.geoqq.data.mate.chat.repository.source.local.model.MateChatEntity
import com.qubacy.geoqq.data.mate.chat.repository.source.local.model.toDataMateChat
import com.qubacy.geoqq.data.mate.chat.repository.source.network.NetworkMateChatDataSource
import com.qubacy.geoqq.data.mate.chat.repository.source.network.model.common.toDataMateChat
import com.qubacy.geoqq.data.mate.chat.repository.source.network.model.response.GetChatsResponse
import com.qubacy.geoqq.data.mate.chat.repository.source.websocket.WebSocketUpdateMateChatDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import retrofit2.Call

class MateChatDataRepository(
    val localMateChatDataSource: LocalMateChatDataSource,
    val networkMateChatDataSource: NetworkMateChatDataSource,
    webSocketUpdateMateChatDataSource: WebSocketUpdateMateChatDataSource
) : UpdatableDataRepository(webSocketUpdateMateChatDataSource) {
    private var mPrevChatCount: Int = 0

    private fun getChatsWithDatabase(count: Int): Result {
        var chatFlow: Flow<List<MateChatEntity>>? = null

        try {
            chatFlow = localMateChatDataSource.getChats(count)
        } catch (e: Exception) {
            return ErrorResult(ErrorContext.Database.UNKNOWN_DATABASE_ERROR.id)
        }

        return GetChatsWithDatabaseResult(chatFlow.map { it.map { it.toDataMateChat() } })
    }

    private fun getChatsWithNetwork(offset: Int, count: Int, accessToken: String): Result {
        val networkCall = networkMateChatDataSource.getChats(offset, count, accessToken) as Call<Response>
        val executeNetworkRequestResult = executeNetworkRequest(networkCall)

        if (executeNetworkRequestResult is ErrorResult) return executeNetworkRequestResult
        if (executeNetworkRequestResult is InterruptionResult) return executeNetworkRequestResult

        val responseBody = (executeNetworkRequestResult as ExecuteNetworkRequestResult)
            .response as GetChatsResponse

        return GetChatsWithNetworkResult(responseBody.chats.map { it.toDataMateChat() })
    }

    private fun insertChatsIntoDatabase(chats: List<DataMateChat>): Result {
        try {
            for (chat in chats) {
                val chatEntity = MateChatEntity(
                    chat.id, chat.userId, chat.newMessageCount, chat.lastMessageId)

                localMateChatDataSource.insertChat(chatEntity)
            }
        } catch (e: Exception) {
            return ErrorResult(ErrorContext.Database.UNKNOWN_DATABASE_ERROR.id)
        }

        return Result()
    }

    suspend fun getChats(accessToken: String, count: Int): Result {
        val getChatsWithDatabaseResult = getChatsWithDatabase(count)

        if (getChatsWithDatabaseResult is ErrorResult) return getChatsWithDatabaseResult
        if (getChatsWithDatabaseResult is InterruptionResult) return getChatsWithDatabaseResult

        val getChatsWithDatabaseResultCast = getChatsWithDatabaseResult as GetChatsWithDatabaseResult

        val curNetworkRequestChatCount = count - mPrevChatCount
        val getChatsWithNetworkResult = getChatsWithNetwork(
            mPrevChatCount, curNetworkRequestChatCount, accessToken)

        if (getChatsWithNetworkResult is ErrorResult) return getChatsWithDatabaseResult
        if (getChatsWithNetworkResult is InterruptionResult) return getChatsWithDatabaseResult

        mPrevChatCount = count
        val getChatsWithNetworkResultCast = getChatsWithNetworkResult as GetChatsWithNetworkResult

        val insertChatsIntoDatabaseResult = insertChatsIntoDatabase(
            getChatsWithNetworkResultCast.chats)

        if (insertChatsIntoDatabaseResult is ErrorResult) return insertChatsIntoDatabaseResult
        if (insertChatsIntoDatabaseResult is InterruptionResult) return insertChatsIntoDatabaseResult

        val startChatsUpdateListeningResult = initUpdateSource()

        if (startChatsUpdateListeningResult is ErrorResult) return startChatsUpdateListeningResult

        return GetChatsResult(getChatsWithDatabaseResultCast.chatFlow)
    }

    override fun processUpdates(updates: List<Update>) {
        TODO("Not yet implemented")
    }
}