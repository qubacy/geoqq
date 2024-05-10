package com.qubacy.geoqq.data._common.repository._common.source.remote.http._common.response.error

class ErrorResponse(
    val error: ErrorResponseContent
) {
    companion object {
        const val ERROR_PROP_NAME = "error"
    }
}

class ErrorResponseContent(
    val id: Long
) {
    companion object {
        const val ID_PROP_NAME = "id"
    }
}