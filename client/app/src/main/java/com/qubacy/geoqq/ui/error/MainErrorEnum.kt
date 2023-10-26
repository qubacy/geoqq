package com.qubacy.geoqq.ui.error

import com.qubacy.geoqq.R
import com.qubacy.geoqq.common.error.Error

enum class MainErrorEnum(val error: Error) {
    IMAGE_PICKING_ERROR(Error(R.string.error_image_picking_failed, Error.Level.NORMAL));
}