package com.qubacy.geoqq.data.myprofile.repository.source.http._common

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MyProfilePrivacy(
    @Json(name = "hit-me-up") val hitMeUpId: Int
) {

}