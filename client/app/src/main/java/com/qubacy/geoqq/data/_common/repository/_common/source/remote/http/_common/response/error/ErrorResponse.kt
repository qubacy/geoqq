package com.qubacy.geoqq.data._common.repository._common.source.remote.http._common.response.error

import com.qubacy.geoqq.data._common.repository._common.source.remote.http._common.response.error.content.ErrorResponseContent

class ErrorResponse(
    val error: ErrorResponseContent
) {
    companion object {
        const val ERROR_PROP_NAME = "error"
    }
}