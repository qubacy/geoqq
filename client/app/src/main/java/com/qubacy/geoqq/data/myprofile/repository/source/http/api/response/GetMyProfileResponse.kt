package com.qubacy.geoqq.data.myprofile.repository.source.http.api.response

import com.qubacy.geoqq.data.myprofile.repository.source.http.api._common.MyProfilePrivacy
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GetMyProfileResponse(
    @Json(name = "login") val login: String,
    @Json(name = "username") val username: String,
    @Json(name = "description") val aboutMe: String,
    @Json(name = "avatar-id") val avatarId: Long,
    @Json(name = "privacy") val privacy: MyProfilePrivacy
) {

}