package com.qubacy.geoqq.data._common.repository.aspect.websocket

import android.util.Log
import com.qubacy.geoqq._common.exception.error.ErrorAppException
import com.qubacy.geoqq.data._common.repository._common.result.DataResult
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.result._common.WebSocketResult
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.result.closed.WebSocketClosedResult
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.result.error.WebSocketErrorResult
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.result.payload.WebSocketPayloadResult

interface WebSocketEventDataRepository {
    companion object {
        const val TAG = "WebSocketEventDataRepository"
    }

    fun mapWebSocketResultToDataResult(
        webSocketResult: WebSocketResult
    ): DataResult? {
        Log.d(TAG, "mapWebSocketResultToDataResult(): webSocketResult.class = ${webSocketResult.javaClass.simpleName};")

        return when (webSocketResult::class) {
            WebSocketClosedResult::class ->
                processWebSocketClosedResult(webSocketResult as WebSocketClosedResult)
            WebSocketErrorResult::class ->
                processWebSocketErrorResult(webSocketResult as WebSocketErrorResult)
            WebSocketPayloadResult::class ->
                processWebSocketPayloadResult(webSocketResult as WebSocketPayloadResult)
            else -> throw IllegalArgumentException()
        }
    }

    fun processWebSocketClosedResult(webSocketClosedResult: WebSocketClosedResult): DataResult? {
        return null // todo: ok?
    }

    fun processWebSocketErrorResult(webSocketErrorResult: WebSocketErrorResult): DataResult? {
        throw ErrorAppException(webSocketErrorResult.error) // todo: ?
    }

    fun processWebSocketPayloadResult(
        webSocketPayloadResult: WebSocketPayloadResult
    ): DataResult? = null
}