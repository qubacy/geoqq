package com.qubacy.geoqq.data.mate.chat.repository.source.http.response

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class GetChatsResponse(
    val chats: List<GetChatResponse>
) {

}