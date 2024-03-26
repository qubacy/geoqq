package com.qubacy.geoqq.data.mate.message.repository

import com.qubacy.geoqq.data._common.model.message.DataMessage
import com.qubacy.geoqq.data._common.model.message.toDataMessage
import com.qubacy.geoqq.data._common.model.message.toMateMessageEntity
import com.qubacy.geoqq.data._common.repository.producing.ProducingDataRepository
import com.qubacy.geoqq.data._common.util.http.executor.executeNetworkRequest
import com.qubacy.geoqq.data.error.repository.ErrorDataRepository
import com.qubacy.geoqq.data.mate.message.repository.result.GetMessagesDataResult
import com.qubacy.geoqq.data.mate.message.repository.source.http.HttpMateMessageDataSource
import com.qubacy.geoqq.data.mate.message.repository.source.local.LocalMateMessageDataSource
import com.qubacy.geoqq.data.mate.message.repository.source.local.entity.toDataMessage
import com.qubacy.geoqq.data.token.repository.TokenDataRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

class MateMessageDataRepository @Inject constructor(
    coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO,
    coroutineScope: CoroutineScope = CoroutineScope(coroutineDispatcher),
    val errorDataRepository: ErrorDataRepository,
    val tokenDataRepository: TokenDataRepository,
    val localMateMessageDataSource: LocalMateMessageDataSource,
    val httpMateMessageDataSource: HttpMateMessageDataSource,
    // todo: provide a websocket data source;
) : ProducingDataRepository(coroutineDispatcher, coroutineScope) {
    suspend fun getMessages(chatId: Long, offset: Int, count: Int) {
        val localMessages = localMateMessageDataSource.getMessages(chatId, offset, count)

        if (localMessages.isNotEmpty()) {
            val localDataMessages = localMessages.map { it.toDataMessage() }

            mResultFlow.emit(GetMessagesDataResult(localDataMessages))
        }

        val accessToken = tokenDataRepository.getTokens().accessToken
        val getMessagesCall = httpMateMessageDataSource
            .getMateMessages(chatId, offset, count, accessToken)
        val getMessagesResponse = executeNetworkRequest(errorDataRepository, getMessagesCall)

        val httpDataMessages = getMessagesResponse.messages.map { it.toDataMessage() }

        mResultFlow.emit(GetMessagesDataResult(httpDataMessages))

        val messagesToSave = httpDataMessages.map { it.toMateMessageEntity(chatId) }

        localMateMessageDataSource.saveMessages(messagesToSave)
    }

    suspend fun sendMessage(chatId: Long, message: DataMessage) {
        // todo: implement using the websocket data source;


    }
}