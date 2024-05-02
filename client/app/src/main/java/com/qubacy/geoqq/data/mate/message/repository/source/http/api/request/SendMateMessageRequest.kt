package com.qubacy.geoqq.data.mate.message.repository.source.http.api.request

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

// todo: delete:
@JsonClass(generateAdapter = true)
class SendMateMessageRequest(
    @Json(name = "text") val text: String
) {

}