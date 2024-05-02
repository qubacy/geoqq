package com.qubacy.geoqq.data._common.repository._common.error.type.token

import com.qubacy.geoqq._common.model.error._common.domain.ErrorDomain
import com.qubacy.geoqq._common.model.error._common.type.ErrorType

enum class DataTokenErrorType(
    override val id: Long,
    override val domain: ErrorDomain = ErrorDomain.DATA_TOKEN
) : ErrorType {
    LOCAL_REFRESH_TOKEN_INVALID(0),
    INVALID_TOKEN_PAYLOAD(1);
}