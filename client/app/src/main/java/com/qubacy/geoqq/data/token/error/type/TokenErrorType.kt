package com.qubacy.geoqq.data.token.error.type

import com.qubacy.geoqq._common.model.error.domain.ErrorDomain
import com.qubacy.geoqq._common.model.error.type.ErrorType

enum class TokenErrorType(
    override val id: Long,
    override val domain: ErrorDomain = ErrorDomain.TOKEN
) : ErrorType {
    LOCAL_REFRESH_TOKEN_INVALID(0),
    INVALID_TOKEN_PAYLOAD(1);

    companion object {
        fun getErrorTypeByErrorCode(errorCode: Long): TokenErrorType {
            return entries.find { it.getErrorCode() ==  errorCode }!!
        }
    }
}