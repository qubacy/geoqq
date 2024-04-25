package com.qubacy.geoqq._common.model.error.auth

import com.qubacy.geoqq._common.model.error._common.domain.ErrorDomain
import com.qubacy.geoqq._common.model.error._common.type.ErrorType

enum class AuthErrorType(
    override val id: Long,
    override val domain: ErrorDomain = ErrorDomain.AUTH
) : ErrorType {
    ERROR_1(1),
    ERROR_2(2);
}