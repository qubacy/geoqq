package com.qubacy.geoqq.data.mate.chat.repository.source.http.request

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class DeleteChatRequest(
    @Json(name = "access-token") val accessToken: String
) {

}