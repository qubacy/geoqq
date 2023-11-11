package com.qubacy.geoqq.data.mate.message.repository

import com.qubacy.geoqq.common.error.ErrorContext
import com.qubacy.geoqq.data.common.repository.common.result.common.Result
import com.qubacy.geoqq.data.common.repository.common.result.error.ErrorResult
import com.qubacy.geoqq.data.common.repository.network.updatable.UpdatableDataRepository
import com.qubacy.geoqq.data.common.repository.network.updatable.source.update.update.Update
import com.qubacy.geoqq.data.mate.message.repository.result.GetMessagesResult
import com.qubacy.geoqq.data.mate.message.repository.result.GetMessagesWithDatabaseResult
import com.qubacy.geoqq.data.mate.message.repository.source.local.LocalMateMessageDataSource
import com.qubacy.geoqq.data.mate.message.repository.source.local.model.MateMessageEntity
import com.qubacy.geoqq.data.mate.message.repository.source.local.model.toDataMessage
import com.qubacy.geoqq.data.mate.message.repository.source.network.NetworkMateMessageDataSource
import com.qubacy.geoqq.data.mate.message.repository.source.websocket.WebSocketUpdateMateMessageDataSource

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


    }

    override fun processUpdates(updates: List<Update>) {
        TODO("Not yet implemented")
    }
}