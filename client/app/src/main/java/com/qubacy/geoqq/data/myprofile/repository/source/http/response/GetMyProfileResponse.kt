package com.qubacy.geoqq.data.myprofile.repository.source.http.response

import com.qubacy.geoqq.data.myprofile.repository.source.http._common.MyProfilePrivacy
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GetMyProfileResponse(
    @Json(name = "username") val username: String,
    @Json(name = "description") val aboutMe: String,
    @Json(name = "avatar-id") val avatarId: Long,
    @Json(name = "privacy") val privacy: MyProfilePrivacy
) {

}