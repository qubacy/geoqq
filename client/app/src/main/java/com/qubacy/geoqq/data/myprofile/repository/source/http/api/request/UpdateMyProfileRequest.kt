package com.qubacy.geoqq.data.myprofile.repository.source.http.api.request

import com.qubacy.geoqq.data.myprofile.repository.source.http.api._common.MyProfilePrivacy
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class UpdateMyProfileRequest(
    @Json(name = "description") val aboutMe: String?,
    @Json(name = "avatar-id") val avatarId: Long?,
    @Json(name = "security") val security: MyProfileSecurityRequest?,
    @Json(name = "privacy") val privacy: MyProfilePrivacy?
) {

}

@JsonClass(generateAdapter = true)
class MyProfileSecurityRequest(
    @Json(name = "password") val passwordHash: String,
    @Json(name = "new-password") val newPasswordHash: String
) {

}
