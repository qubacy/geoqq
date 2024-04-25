package com.qubacy.geoqq._common.model.error.myprofile

import com.qubacy.geoqq._common.model.error._common.domain.ErrorDomain
import com.qubacy.geoqq._common.model.error._common.type.ErrorType

enum class MyProfileErrorType(
     override val id: Long,
     override val domain: ErrorDomain = ErrorDomain.MY_PROFILE
) : ErrorType {
    ERROR_1(1);
}