package com.qubacy.geoqq.ui.application.activity._common.screen.login.error

import com.qubacy.geoqq._common.model.error._common.domain.ErrorDomain
import com.qubacy.geoqq._common.model.error._common.type.ErrorType

enum class UiLoginFragmentErrorType(
    override val id: Long,
    override val domain: ErrorDomain = ErrorDomain.UI_LOGIN
) : ErrorType {
    INVALID_LOGIN_DATA(0);
}