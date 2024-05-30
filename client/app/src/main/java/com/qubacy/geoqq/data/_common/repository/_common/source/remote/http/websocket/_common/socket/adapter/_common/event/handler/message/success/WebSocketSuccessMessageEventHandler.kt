package com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.socket.adapter._common.event.handler.message.success

import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.packet.event.json.adapter.EventJsonAdapter
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.packet.event.json.adapter.callback.EventJsonAdapterCallback
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.packet.event.payload.error.json.adapter.ErrorEventPayloadJsonAdapter
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.socket.adapter._common.event.handler.message._common.WebSocketMessageEventHandler
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.socket.adapter._common.event.handler.message.success.callback.WebSocketSuccessMessageEventHndlrClbck
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.socket.adapter._common.event.model._common.WebSocketEvent
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.socket.adapter._common.event.model.message.WebSocketMessageEvent
import com.squareup.moshi.JsonAdapter
import javax.inject.Inject

class WebSocketSuccessMessageEventHandler @Inject constructor(
    private val mEventJsonAdapter: EventJsonAdapter,
    private val mSuccessEventPayloadJsonAdapter: ErrorEventPayloadJsonAdapter
) : WebSocketMessageEventHandler, EventJsonAdapterCallback {
    companion object {
        const val SUCCESS_POSTFIX = "succeeded"
    }

    private lateinit var mCallback: WebSocketSuccessMessageEventHndlrClbck

    init {
        mEventJsonAdapter.setCallback(this)
    }

    fun setCallback(callback: WebSocketSuccessMessageEventHndlrClbck) {
        mCallback = callback
    }

    override fun handle(event: WebSocketEvent): Boolean {
        if (event !is WebSocketMessageEvent) return false

        val successPayload = mEventJsonAdapter.fromJson(event.message)?.payload ?: return false

        mCallback.onWebSocketMessageSucceeded()

        return true
    }

    override fun getEventPayloadJsonAdapterByType(type: String): JsonAdapter<*>? {
        if (!type.endsWith(SUCCESS_POSTFIX)) return null

        return mSuccessEventPayloadJsonAdapter
    }
}