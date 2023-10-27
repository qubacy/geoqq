package com.qubacy.geoqq.ui.error

import com.qubacy.geoqq.R
import com.qubacy.geoqq.common.error.common.ErrorBase
import com.qubacy.geoqq.common.error.local.LocalError

enum class MainErrorEnum(val error: LocalError) {
    IMAGE_PICKING_ERROR(LocalError(R.string.error_image_picking_failed, ErrorBase.Level.NORMAL));
}