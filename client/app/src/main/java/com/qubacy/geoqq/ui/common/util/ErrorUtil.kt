package com.qubacy.geoqq.ui.common.util

import android.content.res.Resources
import com.qubacy.geoqq.common.error.common.Error
import com.qubacy.geoqq.common.error.common.TypedErrorBase
import com.qubacy.geoqq.common.error.local.LocalError
import com.qubacy.geoqq.common.error.local.toError
import com.qubacy.geoqq.common.error.remote.RemoteError
import com.qubacy.geoqq.common.error.remote.toError

object ErrorUtil {
    fun typedErrorToError(typedError: TypedErrorBase, resources: Resources): Error {
        return when (typedError.errorType) {
            TypedErrorBase.ErrorTypeEnum.LOCAL -> {
                (typedError as LocalError).toError(resources)
            }
            TypedErrorBase.ErrorTypeEnum.REMOTE -> {
                (typedError as RemoteError).toError()
            }
        }
    }
}