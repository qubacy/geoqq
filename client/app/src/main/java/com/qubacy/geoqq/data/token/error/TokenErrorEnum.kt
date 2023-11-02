package com.qubacy.geoqq.data.token.error

import com.qubacy.geoqq.R
import com.qubacy.geoqq.common.error.local.LocalError

enum class TokenErrorEnum(val error: LocalError) {
    LOCAL_REFRESH_TOKEN_NOT_FOUND(LocalError(R.string.error_local_refresh_token_not_found)),
    LOCAL_ACCESS_TOKEN_NOT_FOUND(LocalError(R.string.error_local_access_token_not_found)),
    LOCAL_REFRESH_TOKEN_INVALID(LocalError(R.string.error_local_refresh_token_not_found));
}