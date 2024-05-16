package com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket

import com.qubacy.geoqq._common.struct.flow.MutableColdFlow
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.adapter.event.listener.WebSocketEventListener
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.adapter.event.model._common.WebSocketEvent
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.adapter.event.model.closed.WebSocketClosedEvent
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.adapter.event.model.error.WebSocketErrorEvent
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.adapter.event.model.message.WebSocketMessageEvent
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.event.server.json.adapter.ServerEventJsonAdapter
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.event.server.json.adapter.callback.ServerEventJsonAdapterCallback
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.result._common.WebSocketResult
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.result.closed.WebSocketClosedResult
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.result.error.WebSocketErrorResult
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.result.payload.WebSocketPayloadResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

abstract class RemoteHttpWebSocketDataSource @OptIn(ExperimentalCoroutinesApi::class) constructor(
    coroutineDispatcher: CoroutineDispatcher = Dispatchers.Default.limitedParallelism(1),
    private val mCoroutineScope: CoroutineScope = CoroutineScope(coroutineDispatcher)
) : WebSocketEventListener, ServerEventJsonAdapterCallback {
    private val mServerEventJsonAdapter: ServerEventJsonAdapter

    protected val mEventFlow: MutableColdFlow<WebSocketResult> = MutableColdFlow()
    val eventFlow: Flow<WebSocketResult> get() = mEventFlow.flow

    init {
        mServerEventJsonAdapter = ServerEventJsonAdapter(this)
    }

    override fun onEventGotten(event: WebSocketEvent) {
        mCoroutineScope.launch {
            val result = processEvent(event)

            mEventFlow.emit(result)
        }
    }

    private fun processEvent(event: WebSocketEvent): WebSocketResult {
        return when (event::class) {
            WebSocketClosedEvent::class -> onClosedEventGotten(event as WebSocketClosedEvent)
            WebSocketErrorEvent::class -> onErrorEventGotten(event as WebSocketErrorEvent)
            WebSocketMessageEvent::class -> onMessageEventGotten(event as WebSocketMessageEvent)
            else -> throw IllegalArgumentException()
        }
    }

    private fun onClosedEventGotten(closedEvent: WebSocketClosedEvent): WebSocketClosedResult {
        return WebSocketClosedResult() // todo: shallow for now;
    }

    private fun onErrorEventGotten(errorEvent: WebSocketErrorEvent): WebSocketErrorResult {
        return WebSocketErrorResult(errorEvent.error)
    }

    private fun onMessageEventGotten(messageEvent: WebSocketMessageEvent): WebSocketPayloadResult {
        val serverEvent = mServerEventJsonAdapter.fromJson(messageEvent.message)!!

        return WebSocketPayloadResult(serverEvent.payload)
    }
}