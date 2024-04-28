package com.qubacy.geoqq._common.model.error.auth

import com.qubacy.geoqq._common.model.error._common.domain.ErrorDomain
import com.qubacy.geoqq._common.model.error._common.type.ErrorType

enum class AuthErrorType(
    override val id: Long,
    override val domain: ErrorDomain = ErrorDomain.AUTH
) : ErrorType {
    INVALID_REFRESH_TOKEN(2),
    USER_ALREADY_EXISTS(3),
    INVALID_LOGIN_OR_PASSWORD(4),
    BLOCKED_SIGNING_IN(5);
}