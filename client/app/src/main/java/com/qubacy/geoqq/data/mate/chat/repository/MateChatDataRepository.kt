package com.qubacy.geoqq.data.mate.chat.repository

import com.qubacy.geoqq.data._common.repository.producing.ProducingDataRepository
import com.qubacy.geoqq.data._common.util.http.executor.executeNetworkRequest
import com.qubacy.geoqq.data.error.repository.ErrorDataRepository
import com.qubacy.geoqq.data.mate.chat.model.toDataMateChat
import com.qubacy.geoqq.data.mate.chat.model.toMateChatLastMessageEntityPair
import com.qubacy.geoqq.data.mate.chat.repository.result.GetChatsDataResult
import com.qubacy.geoqq.data.mate.chat.repository.source.http.HttpMateChatDataSource
import com.qubacy.geoqq.data.mate.chat.repository.source.local.LocalMateChatDataSource
import com.qubacy.geoqq.data.token.repository.TokenDataRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class MateChatDataRepository(
    coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO,
    coroutineScope: CoroutineScope = CoroutineScope(coroutineDispatcher),
    val errorDataRepository: ErrorDataRepository,
    val tokenDataRepository: TokenDataRepository,
    val localMateChatDataSource: LocalMateChatDataSource,
    val httpMateChatDataSource: HttpMateChatDataSource,
    // todo: add a websocket source;
) : ProducingDataRepository(coroutineDispatcher, coroutineScope) {
    suspend fun getChats(offset: Int, count: Int) {
        val localChats = localMateChatDataSource.getChats(offset, count)

        if (localChats.isNotEmpty()) {
            val localDataChats = localChats.map { it.toDataMateChat() }

            mResultFlow.emit(GetChatsDataResult(localDataChats))
        }

        val accessToken = tokenDataRepository.getTokens().accessToken
        val getChatsCall = httpMateChatDataSource.getChats(offset, count, accessToken)
        val getChatsResponse = executeNetworkRequest(errorDataRepository, getChatsCall)

        val httpDataChats = getChatsResponse.chats.map { it.toDataMateChat() }

        mResultFlow.emit(GetChatsDataResult(httpDataChats))

        val chatsToSave = httpDataChats.map { it.toMateChatLastMessageEntityPair() }

        localMateChatDataSource.saveChats(chatsToSave)
    }
}