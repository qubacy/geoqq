package com.qubacy.geoqq.data.token.repository.result

import com.qubacy.geoqq.data.common.repository.result.common.Result

data class UpdateAccessTokenResult(
    val refreshToken: String,
    val accessToken: String
) : Result() {

}