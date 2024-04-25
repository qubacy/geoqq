package com.qubacy.geoqq.data._common.repository._common.source.remote.http.response.message

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class GetMessagesResponse(
    val messages: List<GetMessageResponse>
) {

}