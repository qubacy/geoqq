package com.qubacy.geoqq.data.common.repository.source.network.error

import com.qubacy.geoqq.R
import com.qubacy.geoqq.common.error.common.ErrorBase
import com.qubacy.geoqq.common.error.local.LocalError

enum class NetworkDataSourceErrorEnum(val error: LocalError) {
    UNKNOWN_NETWORK_RESPONSE_ERROR(
        LocalError(R.string.error_network_response_unknown_error, ErrorBase.Level.CRITICAL)
    ),
    UNKNOWN_NETWORK_FAILURE(
        LocalError(R.string.error_network_request_unknown_failure, ErrorBase.Level.NORMAL)
    );
}