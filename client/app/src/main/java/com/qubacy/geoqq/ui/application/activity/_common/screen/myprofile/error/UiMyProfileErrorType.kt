package com.qubacy.geoqq.ui.application.activity._common.screen.myprofile.error

import com.qubacy.geoqq._common.model.error._common.domain.ErrorDomain
import com.qubacy.geoqq._common.model.error._common.type.ErrorType

enum class UiMyProfileErrorType(
    override val id: Long,
    override val domain: ErrorDomain = ErrorDomain.UI_MY_PROFILE
) : ErrorType {
    INVALID_UPDATE_DATA(0);
}