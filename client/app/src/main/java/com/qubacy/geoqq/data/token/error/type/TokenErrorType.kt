package com.qubacy.geoqq.data.token.error.type

import com.qubacy.geoqq._common.error.domain.ErrorDomain
import com.qubacy.geoqq._common.error.type.ErrorType

enum class TokenErrorType(
    override val id: Long,
    override val domain: ErrorDomain = ErrorDomain.TOKEN
) : ErrorType {
    LOCAL_REFRESH_TOKEN_NOT_FOUND(0),
    INVALID_TOKEN_PAYLOAD(1);
}