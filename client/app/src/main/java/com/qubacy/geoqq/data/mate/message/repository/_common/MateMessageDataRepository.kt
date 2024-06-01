package com.qubacy.geoqq.data.mate.message.repository._common

import androidx.lifecycle.LiveData
import com.qubacy.geoqq.data._common.repository.aspect.websocket.WebSocketEventDataRepository
import com.qubacy.geoqq.data._common.repository.message.MessageDataRepository
import com.qubacy.geoqq.data._common.repository.producing.ProducingDataRepository
import com.qubacy.geoqq.data.mate.message.repository._common.result.get.GetMessagesDataResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

abstract class MateMessageDataRepository(
    coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO,
    coroutineScope: CoroutineScope = CoroutineScope(coroutineDispatcher)
) : ProducingDataRepository(coroutineDispatcher, coroutineScope),
    MessageDataRepository,
    WebSocketEventDataRepository
{
    abstract suspend fun getMessages(
        chatId: Long,
        loadedMessageIds: List<Long>,
        offset: Int,
        count: Int
    ): LiveData<GetMessagesDataResult>

    abstract suspend fun sendMessage(chatId: Long, text: String)
}