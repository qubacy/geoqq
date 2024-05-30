package com.qubacy.geoqq.data.auth.repository._common.source.remote.http.websocket.impl

import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.result._common.WebSocketResult
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.result.error.WebSocketErrorResult
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.socket.adapter._common.event.model.error.WebSocketErrorEvent
import com.qubacy.geoqq.data.auth.repository._common.source.remote.http.websocket._common.RemoteAuthHttpWebSocketDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi

class RemoteAuthHttpWebSocketDataSourceImpl @OptIn(ExperimentalCoroutinesApi::class) constructor(
    coroutineDispatcher: CoroutineDispatcher = Dispatchers.Default.limitedParallelism(1),
    coroutineScope: CoroutineScope = CoroutineScope(coroutineDispatcher)
) : RemoteAuthHttpWebSocketDataSource(
    coroutineDispatcher, coroutineScope
) {
    override fun processErrorEvent(event: WebSocketErrorEvent): WebSocketResult {
        return WebSocketErrorResult(event.error)
    }
}