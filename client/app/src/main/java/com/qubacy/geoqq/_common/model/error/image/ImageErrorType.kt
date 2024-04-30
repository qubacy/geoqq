package com.qubacy.geoqq._common.model.error.image

import com.qubacy.geoqq._common.model.error._common.domain.ErrorDomain
import com.qubacy.geoqq._common.model.error._common.type.ErrorType

enum class ImageErrorType (
    override val id: Long,
    override val domain: ErrorDomain = ErrorDomain.IMAGE
) : ErrorType {
    IMAGE_NOT_FOUND(4),
    IMAGES_NOT_FOUND(5),
    IMAGE_ADDING_BANNED(6);
}