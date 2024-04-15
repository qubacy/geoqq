package com.qubacy.geoqq.data.mate.message.repository.source.http.request

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

// todo: delete:
@JsonClass(generateAdapter = true)
class SendMateMessageRequest(
    @Json(name = "access-token") val accessToken: String,
    @Json(name = "text") val text: String
) {

}