package com.qubacy.geoqq.common.repository.source.network.model

import com.squareup.moshi.Json

open class Response(
    @field:Json(name = "error") val error: ServerError
) {

}