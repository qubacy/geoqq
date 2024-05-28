package com.qubacy.geoqq.data._common.repository._common.source.remote.http.rest.error.type

import com.qubacy.geoqq._common.model.error._common.domain.ErrorDomain
import com.qubacy.geoqq._common.model.error._common.type.ErrorType

enum class DataHttpRestErrorType(
    override val id: Long,
    override val domain: ErrorDomain = ErrorDomain.DATA_NETWORK_REST
) : ErrorType {
    RESPONSE_ERROR_WITH_SERVER_FAIL(0);
}