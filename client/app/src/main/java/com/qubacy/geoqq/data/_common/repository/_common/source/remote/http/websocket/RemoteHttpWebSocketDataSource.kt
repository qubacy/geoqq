package com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket

import com.qubacy.geoqq._common.struct.flow.MutableColdFlow
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.socket.adapter._common.WebSocketAdapter
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.socket.adapter._common.event.listener.WebSocketEventListener
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.socket.adapter._common.event.model._common.WebSocketEvent
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.socket.adapter._common.event.model.error.WebSocketErrorEvent
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.socket.adapter._common.event.model.message.WebSocketMessageEvent
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.packet.event.json.adapter.EventJsonAdapter
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.packet.event.json.adapter.callback.EventJsonAdapterCallback
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.result._common.WebSocketResult
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.result.error.WebSocketErrorResult
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.result.payload.WebSocketPayloadResult
import com.qubacy.geoqq.data._common.repository.producing.source.ProducingDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

abstract class RemoteHttpWebSocketDataSource @OptIn(ExperimentalCoroutinesApi::class) constructor(
    coroutineDispatcher: CoroutineDispatcher = Dispatchers.Default.limitedParallelism(1),
    private val mCoroutineScope: CoroutineScope = CoroutineScope(coroutineDispatcher)
) : ProducingDataSource, WebSocketEventListener, EventJsonAdapterCallback {
    protected abstract val mWebSocketAdapter: WebSocketAdapter
    protected abstract val mEventJsonAdapter: EventJsonAdapter

    protected val mEventFlow: MutableColdFlow<WebSocketResult> = MutableColdFlow()
    val eventFlow: Flow<WebSocketResult> get() = mEventFlow.flow

    @Volatile
    private var mIsStarted: Boolean = false

    @Synchronized
    override fun startProducing() {
        if (mIsStarted) return

        mIsStarted = true

        mWebSocketAdapter.addEventListener(this)
    }

    @Synchronized
    override fun stopProducing() {
        if (!mIsStarted) return

        mIsStarted = false

        mWebSocketAdapter.removeEventListener(this)
    }

    override fun onEventGotten(event: WebSocketEvent) {
        mCoroutineScope.launch {
            val result = processEvent(event) ?: return@launch

            mEventFlow.emit(result)
        }
    }

    private fun processEvent(event: WebSocketEvent): WebSocketResult? {
        return when (event::class) {
            WebSocketErrorEvent::class -> onErrorEventGotten(event as WebSocketErrorEvent)
            WebSocketMessageEvent::class -> onMessageEventGotten(event as WebSocketMessageEvent)
            else -> throw IllegalArgumentException()
        }
    }

    private fun onErrorEventGotten(errorEvent: WebSocketErrorEvent): WebSocketErrorResult {
        return WebSocketErrorResult(errorEvent.error)
    }

    private fun onMessageEventGotten(messageEvent: WebSocketMessageEvent): WebSocketPayloadResult? {
        val serverEvent = mEventJsonAdapter.fromJson(messageEvent.message) ?: return null

        return WebSocketPayloadResult(serverEvent.header.type, serverEvent.payload)
    }
}