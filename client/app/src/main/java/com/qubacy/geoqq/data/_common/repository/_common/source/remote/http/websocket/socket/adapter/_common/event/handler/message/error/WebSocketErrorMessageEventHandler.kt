package com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.socket.adapter._common.event.handler.message.error

import com.qubacy.geoqq.data._common.repository._common.source.local.database.error._common.LocalErrorDatabaseDataSource
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.event.server.json.adapter.callback.ServerEventJsonAdapterCallback
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.event.server.payload.error.ErrorServerEventPayload
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.event.server.payload.error.json.adapter.ErrorServerEventPayloadJsonAdapter
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.socket.adapter._common.event.handler.message._common.WebSocketMessageEventHandler
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.socket.adapter._common.event.model.message.WebSocketMessageEvent
import com.qubacy.geoqq.data._common.repository.token.repository._common.TokenDataRepository
import com.squareup.moshi.JsonAdapter
import javax.inject.Inject

class WebSocketErrorMessageEventHandler @Inject constructor(
    private val mErrorDataSource: LocalErrorDatabaseDataSource,
    private val mTokenDataRepository: TokenDataRepository,
    private val mServerMessageEventPayloadJsonAdapter: ErrorServerEventPayloadJsonAdapter,
    private val mErrorServerEventPayloadJsonAdapter: ErrorServerEventPayloadJsonAdapter
) : WebSocketMessageEventHandler, ServerEventJsonAdapterCallback {
    companion object {
        const val ERROR_EVENT_TYPE_NAME = "general_error"
    }

    override fun handle(event: WebSocketMessageEvent): Boolean {
        val errorPayload = mServerMessageEventPayloadJsonAdapter
            .fromJson(event.message) ?: return false

        processError(errorPayload)

        return true
    }

    private fun processError(errorPayload: ErrorServerEventPayload) {
        // todo: implement:
        //  1. decide what to do with the error code;
        //  2. decide how to request new tokens;
        //  3. how to return errors to the app;


    }

    override fun getEventPayloadJsonAdapterByType(type: String): JsonAdapter<*>? {
        if (type != ERROR_EVENT_TYPE_NAME) return null

        return mErrorServerEventPayloadJsonAdapter
    }
}