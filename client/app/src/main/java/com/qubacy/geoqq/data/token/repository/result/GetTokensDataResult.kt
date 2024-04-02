package com.qubacy.geoqq.data.token.repository.result

import com.qubacy.geoqq.data._common.repository._common.result.DataResult

data class GetTokensDataResult(
    val accessToken: String,
    val refreshToken: String
) : DataResult {

}