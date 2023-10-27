package com.qubacy.geoqq.data.common.auth.repository.source.network.model

import com.qubacy.geoqq.common.repository.source.network.model.Response
import com.qubacy.geoqq.common.repository.source.network.model.ServerError
import com.squareup.moshi.Json

class AuthResponse(
    @field:Json(name = "accessToken") val accessToken: String,
    @field:Json(name = "refreshToken") val refreshToken: String,
    error: ServerError
) : Response(error) {

}