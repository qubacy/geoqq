package com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.error.type

import com.qubacy.geoqq._common.model.error._common.domain.ErrorDomain
import com.qubacy.geoqq._common.model.error._common.type.ErrorType

enum class DataHttpWebSocketErrorType(
    override val id: Long,
    override val domain: ErrorDomain = ErrorDomain.DATA_NETWORK_WS
) : ErrorType {
    ACTION_FAILED_SERVER_SIDE(0),
    WEB_SOCKET_FAILURE(1),
    ACTION_TIMEOUT(2);
}