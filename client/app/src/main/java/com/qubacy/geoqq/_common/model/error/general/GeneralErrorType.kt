package com.qubacy.geoqq._common.model.error.general

import com.qubacy.geoqq._common.model.error._common.domain.ErrorDomain
import com.qubacy.geoqq._common.model.error._common.type.ErrorType

enum class GeneralErrorType(
    override val id: Long,
    override val domain: ErrorDomain = ErrorDomain.GENERAL
) : ErrorType {
    INVALID_ACCESS_TOKEN(1);
}