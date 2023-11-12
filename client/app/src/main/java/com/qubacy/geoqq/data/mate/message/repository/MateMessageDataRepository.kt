package com.qubacy.geoqq.data.mate.message.repository

import com.qubacy.geoqq.common.error.ErrorContext
import com.qubacy.geoqq.data.common.message.model.DataMessage
import com.qubacy.geoqq.data.common.message.repository.source.network.model.request.SendMessageRequestBody
import com.qubacy.geoqq.data.common.message.repository.source.network.model.request.common.MessageToSend
import com.qubacy.geoqq.data.common.message.repository.source.network.model.response.MessageListResponse
import com.qubacy.geoqq.data.common.message.repository.source.network.model.response.SendMessageResponse
import com.qubacy.geoqq.data.common.message.repository.source.network.model.response.common.toDataMessage
import com.qubacy.geoqq.data.common.repository.common.result.common.Result
import com.qubacy.geoqq.data.common.repository.common.result.error.ErrorResult
import com.qubacy.geoqq.data.common.repository.common.result.interruption.InterruptionResult
import com.qubacy.geoqq.data.common.repository.common.source.network.model.response.common.Response
import com.qubacy.geoqq.data.common.repository.network.common.result.ExecuteNetworkRequestResult
import com.qubacy.geoqq.data.common.repository.network.updatable.UpdatableDataRepository
import com.qubacy.geoqq.data.common.repository.network.updatable.source.update.update.Update
import com.qubacy.geoqq.data.mate.message.repository.result.GetMessagesResult
import com.qubacy.geoqq.data.mate.message.repository.result.GetMessagesWithDatabaseResult
import com.qubacy.geoqq.data.mate.message.repository.result.GetMessagesWithNetworkResult
import com.qubacy.geoqq.data.mate.message.repository.result.InsertMessagesIntoDatabaseResult
import com.qubacy.geoqq.data.mate.message.repository.result.SendMessageResult
import com.qubacy.geoqq.data.mate.message.repository.source.local.LocalMateMessageDataSource
import com.qubacy.geoqq.data.mate.message.repository.source.local.model.MateMessageEntity
import com.qubacy.geoqq.data.mate.message.repository.source.local.model.toDataMessage
import com.qubacy.geoqq.data.mate.message.repository.source.network.NetworkMateMessageDataSource
import com.qubacy.geoqq.data.mate.message.repository.source.websocket.WebSocketUpdateMateMessageDataSource
import retrofit2.Call

class MateMessageDataRepository(
    val localMateMessageDataSource: LocalMateMessageDataSource,
    val networkMateMessageDataSource: NetworkMateMessageDataSource,
    updateMateMessageDataSource: WebSocketUpdateMateMessageDataSource
) : UpdatableDataRepository(updateMateMessageDataSource) {
    private var mPrevMessageCount: Int = 0

    private fun getMessagesWithDatabase(chatId: Long, count: Int): Result {
        var messages: List<MateMessageEntity>? = null

        try {
            messages = localMateMessageDataSource.getMateMessages(chatId, count)

        } catch (e: Exception) {
            return ErrorResult(ErrorContext.Database.UNKNOWN_DATABASE_ERROR.id)
        }

        return GetMessagesWithDatabaseResult(messages.map { it.toDataMessage() })
    }

    private fun getMessagesWithNetwork(
        chatId: Long,
        offset: Int,
        count: Int,
        accessToken: String
    ): Result {
        val networkCall = networkMateMessageDataSource.getMateMessage(
            chatId, offset, count, accessToken) as Call<Response>
        val networkRequestResult = executeNetworkRequest(networkCall)

        if (networkRequestResult is ErrorResult) return networkRequestResult
        if (networkRequestResult is InterruptionResult) return networkRequestResult

        val networkResponse = (networkRequestResult as ExecuteNetworkRequestResult)
            .response as MessageListResponse

        return GetMessagesWithNetworkResult(networkResponse.messages.map{ it.toDataMessage() })
    }

    private fun insertMessagesIntoDatabase(
        chatId: Long,
        messages: List<DataMessage>
    ): Result {
        try {
            for (message in messages) {
                val mateMessageEntity = MateMessageEntity(
                    message.id, chatId, message.userId, message.text, message.time / 1000
                )

                localMateMessageDataSource.insertMateMessage(mateMessageEntity)
            }
        } catch (e: Exception) {
            return ErrorResult(ErrorContext.Database.UNKNOWN_DATABASE_ERROR.id)
        }

        return InsertMessagesIntoDatabaseResult()
    }

    suspend fun getMessages(accessToken: String, chatId: Long, count: Int) {
        val getMessagesWithDatabaseResult = getMessagesWithDatabase(chatId, count)

        if (getMessagesWithDatabaseResult is ErrorResult)
            return emitResult(getMessagesWithDatabaseResult)
        if (getMessagesWithDatabaseResult is InterruptedException)
            return emitResult(getMessagesWithDatabaseResult)

        val getMessagesWithDatabaseResultCast =
            getMessagesWithDatabaseResult as GetMessagesWithDatabaseResult

        if (getMessagesWithDatabaseResult.messages.isNotEmpty())
            emitResult(GetMessagesResult(getMessagesWithDatabaseResultCast.messages))

        val curMessageCount = count - mPrevMessageCount
        val getMessagesWithNetworkResult = getMessagesWithNetwork(
            chatId, mPrevMessageCount, curMessageCount, accessToken)

        if (getMessagesWithNetworkResult is ErrorResult)
            return emitResult(getMessagesWithDatabaseResult)
        if (getMessagesWithNetworkResult is InterruptionResult)
            return emitResult(getMessagesWithNetworkResult)

        mPrevMessageCount = count
        val getMessagesWithNetworkResultCast =
            getMessagesWithNetworkResult as GetMessagesWithNetworkResult

        emitResult(GetMessagesResult(getMessagesWithNetworkResult.messages))

        val insertMessagesIntoDatabaseResult = insertMessagesIntoDatabase(
            chatId, getMessagesWithNetworkResultCast.messages)

        if (insertMessagesIntoDatabaseResult is ErrorResult)
            return emitResult(insertMessagesIntoDatabaseResult)
        if (insertMessagesIntoDatabaseResult is InterruptionResult)
            return emitResult(insertMessagesIntoDatabaseResult)

        val initUpdateSourceResult = initUpdateSource()

        if (initUpdateSourceResult is ErrorResult) return emitResult(initUpdateSourceResult)
    }

    suspend fun sendMessage(
        accessToken: String,
        chatId: Long,
        messageText: String
    ): Result {
        val messageToSend = MessageToSend(messageText)
        val sendMessageRequestBody = SendMessageRequestBody(accessToken, messageToSend)
        val sendMessageNetworkCall = networkMateMessageDataSource
            .sendMateMessage(chatId, sendMessageRequestBody) as Call<Response>
        val sendMessageNetworkCallResult = executeNetworkRequest(sendMessageNetworkCall)

        if (sendMessageNetworkCallResult is ErrorResult) return sendMessageNetworkCallResult
        if (sendMessageNetworkCallResult is InterruptionResult) return sendMessageNetworkCallResult

        val sendMessageNetworkResponse = (sendMessageNetworkCallResult as ExecuteNetworkRequestResult)
            .response as SendMessageResponse

        return SendMessageResult()
    }

    override fun processUpdates(updates: List<Update>) {
        TODO("Not yet implemented")
    }
}