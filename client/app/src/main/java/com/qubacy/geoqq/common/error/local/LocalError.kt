package com.qubacy.geoqq.common.error.local

import android.content.res.Resources
import androidx.annotation.StringRes
import com.qubacy.geoqq.common.error.common.Error
import com.qubacy.geoqq.common.error.common.TypedErrorBase

class LocalError(
    @StringRes val messageResId: Int,
    level: Level = Level.NORMAL
) : TypedErrorBase(ErrorTypeEnum.LOCAL, level) {

}

fun LocalError.toError(resources: Resources): Error {
    val message = resources.getString(messageResId)

    return Error(message, level)
}