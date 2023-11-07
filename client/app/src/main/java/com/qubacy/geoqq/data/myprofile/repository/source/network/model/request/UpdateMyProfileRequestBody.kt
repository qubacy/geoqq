package com.qubacy.geoqq.data.myprofile.repository.source.network.model.request

import com.qubacy.geoqq.data.myprofile.repository.source.network.model.common.Privacy
import com.qubacy.geoqq.data.myprofile.repository.source.network.model.request.common.Security
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class UpdateMyProfileRequestBody(
    @Json(name = "access-token") val accessToken: String,

    @Json(name = "description") val description: String?,
    val avatar: String?,
    val privacy: Privacy?,
    val security: Security?
) {

}