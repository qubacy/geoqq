package com.qubacy.geoqq.data._common.error.type

import com.qubacy.geoqq._common.model.error.domain.ErrorDomain
import com.qubacy.geoqq._common.model.error.type.ErrorType

enum class NetworkErrorType(
    override val id: Long,
    override val domain: ErrorDomain = ErrorDomain.NETWORK
) : ErrorType {
    RESPONSE_ERROR(0),
    NULL_RESPONSE_BODY(1),
    REQUEST_FAILED(2);
}