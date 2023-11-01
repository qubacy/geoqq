package com.qubacy.geoqq.data.image.error

import com.qubacy.geoqq.R
import com.qubacy.geoqq.common.error.common.ErrorBase
import com.qubacy.geoqq.common.error.common.TypedErrorBase
import com.qubacy.geoqq.common.error.local.LocalError

enum class ImageErrorEnum(val error: TypedErrorBase) {
    IMAGE_LOADING_FAILED(LocalError(R.string.error_image_loading_failed, ErrorBase.Level.CRITICAL)),
    IMAGE_SAVING_FAILED(LocalError(R.string.error_image_saving_failed, ErrorBase.Level.CRITICAL)),
    IMAGE_DECODING_FAILED(LocalError(R.string.error_image_decoding_failed, ErrorBase.Level.CRITICAL));
}