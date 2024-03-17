package com.qubacy.geoqq.ui.application.activity._common.screen.login.error

import com.qubacy.geoqq._common.error.domain.ErrorDomain
import com.qubacy.geoqq._common.error.type.ErrorType

enum class LoginFragmentErrorType(
    override val id: Long,
    override val domain: ErrorDomain = ErrorDomain.UI
) : ErrorType {
    INVALID_LOGIN_DATA(0);
}