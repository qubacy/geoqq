package com.qubacy.geoqq.data.mate.request.repository.source.network.model.response

import com.qubacy.geoqq.data.common.repository.common.source.network.model.response.common.Response
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class GetMateRequestCountResponse(
    val count: Int
) : Response() {

}