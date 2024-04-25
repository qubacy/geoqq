package com.qubacy.geoqq.data._common.repository._common.source.remote._common.error.type

import com.qubacy.geoqq._common.model.error._common.domain.ErrorDomain
import com.qubacy.geoqq._common.model.error._common.type.ErrorType

enum class DataNetworkErrorType(
    override val id: Long,
    override val domain: ErrorDomain = ErrorDomain.DATA_NETWORK
) : ErrorType {
    REQUEST_FAILED(0),
    RESPONSE_ERROR_WITH_SERVER_FAIL(1);
}