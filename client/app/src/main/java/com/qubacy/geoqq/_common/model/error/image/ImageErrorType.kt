package com.qubacy.geoqq._common.model.error.image

import com.qubacy.geoqq._common.model.error._common.domain.ErrorDomain
import com.qubacy.geoqq._common.model.error._common.type.ErrorType

enum class ImageErrorType (
    override val id: Long,
    override val domain: ErrorDomain = ErrorDomain.IMAGE
) : ErrorType {
    ERROR_1(1),
    ERROR_2(2);
}