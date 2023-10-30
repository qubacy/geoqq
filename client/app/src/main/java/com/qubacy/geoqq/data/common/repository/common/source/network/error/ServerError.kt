package com.qubacy.geoqq.data.common.repository.common.source.network.error

import com.qubacy.geoqq.common.error.common.ErrorBase
import com.qubacy.geoqq.common.error.remote.RemoteError
import com.qubacy.geoqq.data.common.repository.common.source.network.model.response.common.Response
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

// todo: reconsider this:
@JsonClass(generateAdapter = true)
class ServerError(
    val message: String,
    @Json(name = "level") val levelId: Int
) : Response() {

}

// todo: think of this:
fun ServerError.toRemoteError(): RemoteError {
    val level = ErrorBase.Level.values().find { it.id ==  levelId }!!

    return RemoteError(message, level)
}