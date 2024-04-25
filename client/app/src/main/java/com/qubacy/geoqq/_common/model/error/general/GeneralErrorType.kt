package com.qubacy.geoqq._common.model.error.general

import com.qubacy.geoqq._common.model.error._common.domain.ErrorDomain
import com.qubacy.geoqq._common.model.error._common.type.ErrorType

enum class GeneralErrorType(
    override val id: Long,
    override val domain: ErrorDomain = ErrorDomain.GENERAL
) : ErrorType {
    ERROR_1(0),
    ERROR_2(1);
}