package com.qubacy.geoqq.data.mate.chat.repository._common

import androidx.lifecycle.LiveData
import com.qubacy.geoqq.data._common.repository.aspect.websocket.WebSocketEventDataRepository
import com.qubacy.geoqq.data._common.repository.producing.ProducingDataRepository
import com.qubacy.geoqq.data.mate.chat.repository._common.result.GetChatByIdDataResult
import com.qubacy.geoqq.data.mate.chat.repository._common.result.GetChatsDataResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

abstract class MateChatDataRepository(
    coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO,
    coroutineScope: CoroutineScope = CoroutineScope(coroutineDispatcher)
) : ProducingDataRepository(coroutineDispatcher, coroutineScope), WebSocketEventDataRepository {
    abstract suspend fun getChats(
        loadedChatIds: List<Long>,
        offset: Int,
        count: Int
    ): LiveData<GetChatsDataResult>
    abstract suspend fun getChatById(chatId: Long): LiveData<GetChatByIdDataResult?>
    abstract suspend fun deleteChat(chatId: Long)
}