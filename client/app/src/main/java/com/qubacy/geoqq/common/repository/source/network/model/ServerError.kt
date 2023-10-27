package com.qubacy.geoqq.common.repository.source.network.model

import com.qubacy.geoqq.common.error.common.ErrorBase
import com.qubacy.geoqq.common.error.remote.RemoteError
import com.squareup.moshi.Json

// todo: reconsider this:
class ServerError(
    @field:Json(name = "message") val message: String,
    @field:Json(name = "level") val levelId: Int
) {

}

// todo: think of this:
fun ServerError.toRemoteError(): RemoteError {
    val level = ErrorBase.Level.values().find { it.id ==  levelId }!!

    return RemoteError(message, level)
}