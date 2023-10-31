package com.qubacy.geoqq.data.token.repository.result

import com.qubacy.geoqq.data.common.repository.common.result.common.Result

data class CheckRefreshTokenValidityResult(
    val isValid: Boolean
) : Result() {

}