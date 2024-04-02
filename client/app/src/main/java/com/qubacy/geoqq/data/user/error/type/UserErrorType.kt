package com.qubacy.geoqq.data.user.error.type

import com.qubacy.geoqq._common.model.error.domain.ErrorDomain
import com.qubacy.geoqq._common.model.error.type.ErrorType

enum class UserErrorType (
    override val id: Long,
    override val domain: ErrorDomain = ErrorDomain.USER
) : ErrorType {
    USERS_GETTING_FAILURE(0);
}