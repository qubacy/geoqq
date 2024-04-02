package com.qubacy.geoqq.data.myprofile.repository.source.http.request

import com.qubacy.geoqq.data.myprofile.repository.source.http._common.MyProfilePrivacy
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class UpdateMyProfileRequest(
    @Json(name = "access-token") val accessToken: String,
    @Json(name = "description") val aboutMe: String,
    @Json(name = "avatar-id") val avatarId: Long,
    @Json(name = "security") val security: MyProfileSecurityRequest,
    @Json(name = "privacy") val privacy: MyProfilePrivacy
) {

}

@JsonClass(generateAdapter = true)
class MyProfileSecurityRequest(
    @Json(name = "password") val password: String,
    @Json(name = "new-password") val newPassword: String
) {

}
