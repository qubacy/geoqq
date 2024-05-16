package com.qubacy.geoqq.data._common.repository.aspect.websocket

import com.qubacy.geoqq._common.exception.error.ErrorAppException
import com.qubacy.geoqq.data._common.repository._common.result.DataResult
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.result._common.WebSocketResult
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.result.closed.WebSocketClosedResult
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.result.error.WebSocketErrorResult
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.result.payload.WebSocketPayloadResult

interface WebSocketEventDataRepository {
    fun mapWebSocketResultToDataResult(
        webSocketResult: WebSocketResult
    ): DataResult? {
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
    ): DataResult?
}