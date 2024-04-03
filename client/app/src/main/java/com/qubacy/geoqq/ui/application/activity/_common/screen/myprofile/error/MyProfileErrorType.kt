package com.qubacy.geoqq.ui.application.activity._common.screen.myprofile.error

import com.qubacy.geoqq._common.model.error.domain.ErrorDomain
import com.qubacy.geoqq._common.model.error.type.ErrorType

enum class MyProfileErrorType(
    override val id: Long,
    override val domain: ErrorDomain = ErrorDomain.UI_MY_PROFILE
) : ErrorType {
    INVALID_UPDATE_DATA(0);
}