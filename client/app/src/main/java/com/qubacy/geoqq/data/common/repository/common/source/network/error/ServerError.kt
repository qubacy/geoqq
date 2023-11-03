package com.qubacy.geoqq.data.common.repository.common.source.network.error

import com.qubacy.geoqq.data.common.repository.common.source.network.model.response.common.Response
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class ServerError(
    val id: Long
) : Response() {

}