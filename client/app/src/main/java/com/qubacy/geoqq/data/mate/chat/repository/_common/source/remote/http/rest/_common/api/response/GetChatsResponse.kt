package com.qubacy.geoqq.data.mate.chat.repository._common.source.remote.http.rest._common.api.response

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class GetChatsResponse(
    val chats: List<GetChatResponse>
) {

}