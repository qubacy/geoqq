package com.qubacy.geoqq.data.mate.chat.repository.source.network.model.response

import com.qubacy.geoqq.data.common.repository.common.source.network.model.response.common.Response
import com.qubacy.geoqq.data.mate.chat.repository.source.network.model.common.ChatNetworkModel
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class GetChatsResponse(
    val chats: List<ChatNetworkModel>
) : Response() {

}