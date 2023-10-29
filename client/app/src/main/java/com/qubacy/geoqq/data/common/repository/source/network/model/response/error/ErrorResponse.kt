package com.qubacy.geoqq.data.common.repository.source.network.model.response.error

import com.qubacy.geoqq.data.common.repository.source.network.error.ServerError
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class ErrorResponse(
    val error: ServerError
) {

}