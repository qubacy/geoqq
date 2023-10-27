package com.qubacy.geoqq.data.common.auth.repository.error

import com.qubacy.geoqq.R
import com.qubacy.geoqq.common.error.common.ErrorBase
import com.qubacy.geoqq.common.error.local.LocalError

enum class AuthDataErrorEnum(val error: LocalError) {
    LOCAL_REFRESH_TOKEN_NOT_FOUND(
        LocalError(R.string.error_local_refresh_token_not_found, ErrorBase.Level.NORMAL)),
    UNKNOWN_NETWORK_RESPONSE_ERROR(
        LocalError(R.string.error_network_response_unknown_error, ErrorBase.Level.CRITICAL)
    );
}