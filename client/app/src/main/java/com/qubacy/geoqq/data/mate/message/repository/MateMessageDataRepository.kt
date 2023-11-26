package com.qubacy.geoqq.data.mate.message.repository

import android.util.Log
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
import com.qubacy.geoqq.data.mate.message.repository.result.GetMessagesWithNetworkAndSaveResult
import com.qubacy.geoqq.data.mate.message.repository.result.InsertOrUpdateMessagesEntitiesWithDatabaseResult
import com.qubacy.geoqq.data.mate.message.repository.result.SendMessageResult
import com.qubacy.geoqq.data.mate.message.repository.source.local.LocalMateMessageDataSource
import com.qubacy.geoqq.data.mate.message.repository.source.local.model.MateMessageEntity
import com.qubacy.geoqq.data.mate.message.repository.source.local.model.toDataMessage
import com.qubacy.geoqq.data.mate.message.repository.source.network.NetworkMateMessageDataSource
import com.qubacy.geoqq.data.mate.message.repository.source.websocket.WebSocketUpdateMateMessageDataSource
import retrofit2.Call

open class MateMessageDataRepository(
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

    private fun getMessagesWithNetworkAndSave(
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
        val messagesFromNetwork = networkResponse.messages.map{ it.toDataMessage() }

        val insertOrUpdateMessagesEntitiesWithDatabaseResult =
            insertOrUpdateMessagesEntitiesWithDatabase(chatId, messagesFromNetwork)

        if (insertOrUpdateMessagesEntitiesWithDatabaseResult is ErrorResult)
            return insertOrUpdateMessagesEntitiesWithDatabaseResult
        if (insertOrUpdateMessagesEntitiesWithDatabaseResult is InterruptionResult)
            return insertOrUpdateMessagesEntitiesWithDatabaseResult

        val insertOrUpdateMessagesEntitiesWithDatabaseResultCast =
            insertOrUpdateMessagesEntitiesWithDatabaseResult
                    as InsertOrUpdateMessagesEntitiesWithDatabaseResult

        return GetMessagesWithNetworkAndSaveResult(
            messagesFromNetwork,
            insertOrUpdateMessagesEntitiesWithDatabaseResultCast.areInsertedOrUpdated
        )
    }

    private fun insertOrUpdateMessagesEntitiesWithDatabase(
        chatId: Long,
        messages: List<DataMessage>
    ): Result {
        var updatedMessagesCount = 0

        try {
            for (message in messages) {
                val mateMessageEntity = MateMessageEntity(
                    message.id, chatId, message.userId, message.text, message.time / 1000
                )

                val gottenMateMessage = localMateMessageDataSource.getMateMessage(chatId, message.id)

                if (gottenMateMessage == mateMessageEntity) continue

                if (gottenMateMessage == null)
                    localMateMessageDataSource.insertMateMessage(mateMessageEntity)
                else
                    localMateMessageDataSource.updateMateMessage(mateMessageEntity)

                updatedMessagesCount++
            }
        } catch (e: Exception) {
            return ErrorResult(ErrorContext.Database.UNKNOWN_DATABASE_ERROR.id)
        }

        return InsertOrUpdateMessagesEntitiesWithDatabaseResult(
            updatedMessagesCount > 0
        )
    }

    suspend fun getMessages(accessToken: String, chatId: Long, count: Int) {
        val getMessagesWithDatabaseResult = getMessagesWithDatabase(chatId, count)

        if (getMessagesWithDatabaseResult is ErrorResult)
            return emitResult(getMessagesWithDatabaseResult)
        if (getMessagesWithDatabaseResult is InterruptedException)
            return emitResult(getMessagesWithDatabaseResult)

        val getMessagesWithDatabaseResultCast =
            getMessagesWithDatabaseResult as GetMessagesWithDatabaseResult

        if (getMessagesWithDatabaseResultCast.messages.isNotEmpty())
            emitResult(GetMessagesResult(getMessagesWithDatabaseResultCast.messages, true))

        val curMessageCount = count - mPrevMessageCount
        val getMessagesWithNetworkResult = getMessagesWithNetworkAndSave(
            chatId, mPrevMessageCount, curMessageCount, accessToken)

        if (getMessagesWithNetworkResult is ErrorResult)
            return emitResult(getMessagesWithDatabaseResult)
        if (getMessagesWithNetworkResult is InterruptionResult)
            return emitResult(getMessagesWithNetworkResult)

        mPrevMessageCount = count
        val getMessagesWithNetworkResultCast =
            getMessagesWithNetworkResult as GetMessagesWithNetworkAndSaveResult

        if (getMessagesWithNetworkResultCast.areUpdated)
            emitResult(GetMessagesResult(getMessagesWithNetworkResultCast.messages, false))

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