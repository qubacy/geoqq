package com.qubacy.geoqq.domain.common.error

import com.qubacy.geoqq.R
import com.qubacy.geoqq.common.error.common.TypedErrorBase
import com.qubacy.geoqq.common.error.local.LocalError

enum class ErrorEnum(val error: TypedErrorBase) {
    INVALID_TOKEN(LocalError(R.string.error_invalid_token))
}