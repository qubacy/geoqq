package com.qubacy.geoqq.data.image.error.type

import com.qubacy.geoqq._common.model.error._common.domain.ErrorDomain
import com.qubacy.geoqq._common.model.error._common.type.ErrorType

enum class DataImageLocalErrorType (
    override val id: Long,
    override val domain: ErrorDomain = ErrorDomain.DATA_IMAGE_LOCAL
) : ErrorType {
    SAVING_FAILED(0),
    LOADING_DATA_FAILED(1);
}