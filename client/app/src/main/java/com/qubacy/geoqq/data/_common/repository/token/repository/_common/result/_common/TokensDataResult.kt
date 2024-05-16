package com.qubacy.geoqq.data._common.repository.token.repository._common.result._common

import com.qubacy.geoqq.data._common.repository._common.result.DataResult

abstract class TokensDataResult(
    val refreshToken: String,
    val accessToken: String
) : DataResult {

}