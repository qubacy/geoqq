package com.qubacy.geoqq.data.mate.chat.repository

import com.qubacy.geoqq.common.error.ErrorContext
import com.qubacy.geoqq.data.common.repository.common.result.common.Result
import com.qubacy.geoqq.data.common.repository.common.result.error.ErrorResult
import com.qubacy.geoqq.data.common.repository.common.result.interruption.InterruptionResult
import com.qubacy.geoqq.data.common.repository.common.source.network.model.response.common.Response
import com.qubacy.geoqq.data.common.repository.network.NetworkDataRepository
import com.qubacy.geoqq.data.common.repository.network.result.ExecuteNetworkRequestResult
import com.qubacy.geoqq.data.mate.chat.repository.result.GetChatsWithDatabaseResult
import com.qubacy.geoqq.data.mate.chat.repository.result.GetChatsWithNetworkResult
import com.qubacy.geoqq.data.mate.chat.repository.source.local.LocalMateChatDataSource
import com.qubacy.geoqq.data.mate.chat.repository.source.local.model.ChatEntity
import com.qubacy.geoqq.data.mate.chat.repository.source.local.model.toDataMateChat
import com.qubacy.geoqq.data.mate.chat.repository.source.network.NetworkMateChatDataSource
import com.qubacy.geoqq.data.mate.chat.repository.source.network.model.common.toDataMateChat
import com.qubacy.geoqq.data.mate.chat.repository.source.network.model.response.GetChatsResponse
import com.qubacy.geoqq.data.user.repository.result.GetUserWithDatabaseResult
import retrofit2.Call

class MateChatDataRepository(
    val localMateChatDataSource: LocalMateChatDataSource,
    val networkMateChatDataSource: NetworkMateChatDataSource
) : NetworkDataRepository() {
    private fun getChatsWithDatabase(): Result {
        var chats: List<ChatEntity>? = null

        try {
            chats = localMateChatDataSource.getChats()
        } catch (e: Exception) {
            return ErrorResult(ErrorContext.Database.UNKNOWN_DATABASE_ERROR.id)
        }

        return GetChatsWithDatabaseResult(chats.map { it.toDataMateChat() })
    }

    private fun getChatsWithNetwork(accessToken: String): Result {
        val networkCall = networkMateChatDataSource.getChats(accessToken) as Call<Response>
        val executeNetworkRequestResult = executeNetworkRequest(networkCall)

        if (executeNetworkRequestResult is ErrorResult) return executeNetworkRequestResult
        if (executeNetworkRequestResult is InterruptionResult) return executeNetworkRequestResult

        val responseBody = (executeNetworkRequestResult as ExecuteNetworkRequestResult)
            .response as GetChatsResponse

        return GetChatsWithNetworkResult(responseBody.chats.map { it.toDataMateChat() })
    }

    suspend fun getChats(accessToken: String) {
        val getChatsWithDatabaseResult = getChatsWithDatabase()

        if (getChatsWithDatabaseResult is ErrorResult) return getChatsWithDatabaseResult
        if (getChatsWithDatabaseResult is InterruptionResult) return getChatsWithDatabaseResult

        val getChatsWithDatabaseResultCast = getChatsWithDatabaseResult as GetUserWithDatabaseResult

        val getChatsWithNetworkResult = getChatsWithNetwork(accessToken)

        if (getChatsWithNetworkResult is ErrorResult) return getChatsWithDatabaseResult
        if (getChatsWithNetworkResult is InterruptionResult) return getChatsWithDatabaseResult


    }
}