package com.qubacy.geoqq._common.model.error.user

import com.qubacy.geoqq._common.model.error._common.domain.ErrorDomain
import com.qubacy.geoqq._common.model.error._common.type.ErrorType

enum class UserErrorType (
    override val id: Long,
    override val domain: ErrorDomain = ErrorDomain.USER
) : ErrorType {
    USER_NOT_FOUND(1),
    USERS_NOT_FOUND(2);
}