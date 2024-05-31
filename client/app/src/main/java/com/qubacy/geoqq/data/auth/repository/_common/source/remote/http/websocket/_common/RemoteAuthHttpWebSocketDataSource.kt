package com.qubacy.geoqq.data.auth.repository._common.source.remote.http.websocket._common

import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.RemoteHttpWebSocketDataSource
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.result._common.WebSocketResult
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.socket.adapter._common.WebSocketAdapter
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.socket.adapter._common.event.model._common.WebSocketEvent
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.socket.adapter._common.event.model.error.WebSocketErrorEvent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi

abstract class RemoteAuthHttpWebSocketDataSource @OptIn(ExperimentalCoroutinesApi::class) constructor(
    coroutineDispatcher: CoroutineDispatcher = Dispatchers.Default.limitedParallelism(1),
    coroutineScope: CoroutineScope = CoroutineScope(coroutineDispatcher)
) : RemoteHttpWebSocketDataSource(coroutineDispatcher, coroutineScope) {
    /**
     * This one is implemented this way coz the adapter isn't created before AuthDataRepository;
     */
    fun setWebSocketAdapter(webSocketAdapter: WebSocketAdapter) {
        mWebSocketAdapter = webSocketAdapter

        mWebSocketAdapter.addEventListener(this)
    }

    override fun processEvent(event: WebSocketEvent): WebSocketResult? {
        if (event !is WebSocketErrorEvent) return null

        return processErrorEvent(event)
    }

    protected abstract fun processErrorEvent(event: WebSocketErrorEvent): WebSocketResult?
}