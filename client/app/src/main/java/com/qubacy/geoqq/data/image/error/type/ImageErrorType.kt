package com.qubacy.geoqq.data.image.error.type

import com.qubacy.geoqq._common.model.error.domain.ErrorDomain
import com.qubacy.geoqq._common.model.error.type.ErrorType

enum class ImageErrorType (
    override val id: Long,
    override val domain: ErrorDomain = ErrorDomain.IMAGE
) : ErrorType {
    SAVING_FAILED(0),
    LOADING_DATA_FAILED(1);
}