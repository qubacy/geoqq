package com.qubacy.geoqq.data.myprofile.repository.source.network.model.common

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Privacy(
    @Json(name = "hit-me-up") val hitMeUp: Int
)