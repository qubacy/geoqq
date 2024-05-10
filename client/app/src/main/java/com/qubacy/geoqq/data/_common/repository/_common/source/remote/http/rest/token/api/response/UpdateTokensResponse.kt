package com.qubacy.geoqq.data._common.repository._common.source.remote.http.rest.token.api.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UpdateTokensResponse(
    @Json(name = ACCESS_TOKEN_PROP_NAME) val accessToken: String,
    @Json(name = REFRESH_TOKEN_PROP_NAME) val refreshToken: String
) {
    companion object {
        const val ACCESS_TOKEN_PROP_NAME = "access-token"
        const val REFRESH_TOKEN_PROP_NAME = "refresh-token"
    }
}