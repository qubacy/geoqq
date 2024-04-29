package com.qubacy.geoqq.data._common.repository.message.source.remote.http.response

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class GetMessagesResponse(
    val messages: List<GetMessageResponse>
) {

}