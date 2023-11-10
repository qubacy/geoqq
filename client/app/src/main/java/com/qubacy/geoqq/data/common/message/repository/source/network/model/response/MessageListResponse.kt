package com.qubacy.geoqq.data.common.message.repository.source.network.model.response

import com.qubacy.geoqq.data.common.message.repository.source.network.model.response.common.MessageNetworkModel
import com.qubacy.geoqq.data.common.repository.common.source.network.model.response.common.Response
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class MessageListResponse(
    val messages: List<MessageNetworkModel>
) : Response() {

}