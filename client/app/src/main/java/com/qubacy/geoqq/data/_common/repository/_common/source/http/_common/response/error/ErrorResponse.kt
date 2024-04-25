package com.qubacy.geoqq.data._common.repository._common.source.http._common.response.error

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class ErrorResponse(
    @Json(name = ERROR_PROP_NAME) val error: ErrorResponseContent
) {
    companion object {
        const val ERROR_PROP_NAME = "error"
    }
}

@JsonClass(generateAdapter = true)
class ErrorResponseContent(
    @Json(name = ID_PROP_NAME) val id: Long
) {
    companion object {
        const val ID_PROP_NAME = "id"
    }
}