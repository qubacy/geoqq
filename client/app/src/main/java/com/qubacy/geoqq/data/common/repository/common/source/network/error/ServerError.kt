package com.qubacy.geoqq.data.common.repository.common.source.network.error

import com.qubacy.geoqq.data.common.repository.common.source.network.model.response.common.Response
import com.squareup.moshi.JsonClass

// todo: reconsider this:
@JsonClass(generateAdapter = true)
class ServerError(
    val id: Long
) : Response() {

}
//
//// todo: think of this:
//fun ServerError.toRemoteError(): RemoteError {
//    val level = ErrorBase.Level.values().find { it.id ==  levelId }!!
//
//    return RemoteError(message, level)
//}