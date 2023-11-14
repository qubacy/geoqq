package com.qubacy.geoqq.data.user.repository.source.network.response

import com.qubacy.geoqq.data.common.repository.common.source.network.model.response.common.Response
import com.qubacy.geoqq.data.user.model.DataUser
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class GetUserResponse(
    val id: Long,
    val username: String,
    val description: String,
    @Json(name = "avatar-id") val avatarId: Long,
    @Json(name = "is-mate") val isMate: Boolean
) : Response() {

}

fun GetUserResponse.toDataUser(): DataUser {
    return DataUser(id, username, description, avatarId, isMate)
}