package com.qubacy.geoqq.common.error.remote

import com.qubacy.geoqq.common.error.common.Error
import com.qubacy.geoqq.common.error.common.TypedErrorBase

class RemoteError(
    val message: String,
    level: Level = Level.NORMAL
) : TypedErrorBase(ErrorTypeEnum.REMOTE, level) {

}

fun RemoteError.toError(): Error {
    return Error(message, level)
}