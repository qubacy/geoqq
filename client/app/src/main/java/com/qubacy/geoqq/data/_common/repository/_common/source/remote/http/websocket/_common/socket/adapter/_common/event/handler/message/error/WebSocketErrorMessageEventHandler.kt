package com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.socket.adapter._common.event.handler.message.error

import com.qubacy.geoqq._common.exception.error.ErrorAppException
import com.qubacy.geoqq._common.model.error.auth.AuthErrorType
import com.qubacy.geoqq._common.model.error.general.GeneralErrorType
import com.qubacy.geoqq.data._common.repository._common.source.local.database.error._common.LocalErrorDatabaseDataSource
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.error.type.DataHttpWebSocketErrorType
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.packet.event.json.adapter.EventJsonAdapter
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.packet.event.json.adapter.callback.EventJsonAdapterCallback
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.packet.event.payload.error.ErrorEventPayload
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.packet.event.payload.error.json.adapter.ErrorEventPayloadJsonAdapter
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.socket.adapter._common.event.handler.message._common.WebSocketMessageEventHandler
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.socket.adapter._common.event.handler.message.error.callback.WebSocketErrorMessageEventHandlerCallback
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.socket.adapter._common.event.model._common.WebSocketEvent
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.socket.adapter._common.event.model.message.WebSocketMessageEvent
import com.qubacy.geoqq.data._common.repository.token.error.type.DataTokenErrorType
import com.qubacy.geoqq.data._common.repository.token.repository._common.TokenDataRepository
import com.squareup.moshi.JsonAdapter
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class WebSocketErrorMessageEventHandler @Inject constructor(
    private val mErrorDataSource: LocalErrorDatabaseDataSource,
    private val mTokenDataRepository: TokenDataRepository,
    private val mEventJsonAdapter: EventJsonAdapter,
    private val mErrorEventPayloadJsonAdapter: ErrorEventPayloadJsonAdapter
) : WebSocketMessageEventHandler, EventJsonAdapterCallback {
    companion object {
        const val ERROR_EVENT_TYPE_NAME = "general_error"

        const val CLIENT_SIDE_ERROR_CODE = 400L
        const val SERVER_SIDE_ERROR_CODE = 500L

        val SHUTDOWN_ERRORS = listOf(
            DataTokenErrorType.LOCAL_REFRESH_TOKEN_INVALID.getErrorCode(),
            AuthErrorType.INVALID_REFRESH_TOKEN.getErrorCode()
        )
    }

    private lateinit var mCallback: WebSocketErrorMessageEventHandlerCallback

    init {
        mEventJsonAdapter.setCallback(this)
    }

    fun setCallback(callback: WebSocketErrorMessageEventHandlerCallback) {
        mCallback = callback
    }

    override fun handle(event: WebSocketEvent): Boolean {
        if (event !is WebSocketMessageEvent) return false

        val errorPayload = mEventJsonAdapter.fromJson(event.message)?.payload ?: return false

        processError(errorPayload as ErrorEventPayload)

        return true
    }

    private fun processError(errorPayload: ErrorEventPayload) {
        if (errorPayload.code == SERVER_SIDE_ERROR_CODE)
            throw ErrorAppException(mErrorDataSource.getError(
                DataHttpWebSocketErrorType.ACTION_FAILED_SERVER_SIDE.getErrorCode()))

        try {
            when (errorPayload.error.id) {
                GeneralErrorType.INVALID_ACCESS_TOKEN.getErrorCode() -> processInvalidTokenError()
            }
        } catch (e: ErrorAppException) {
            if (e.error.isCritical) throw e
            if (e.error.id in SHUTDOWN_ERRORS) return mCallback.shutdownWebSocketWithError(e.error)
        }
    }

    private fun processInvalidTokenError() = runBlocking {
        mTokenDataRepository.updateTokens()
        mCallback.retryActionSending() // todo: good?
    }

    override fun getEventPayloadJsonAdapterByType(type: String): JsonAdapter<*>? {
        if (type != ERROR_EVENT_TYPE_NAME) return null

        return mErrorEventPayloadJsonAdapter
    }
}