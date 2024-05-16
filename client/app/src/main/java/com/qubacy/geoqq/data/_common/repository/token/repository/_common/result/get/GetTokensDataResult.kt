package com.qubacy.geoqq.data._common.repository.token.repository._common.result.get

import com.qubacy.geoqq.data._common.repository.token.repository._common.result._common.TokensDataResult

class GetTokensDataResult(
    refreshToken: String,
    accessToken: String
) : TokensDataResult(refreshToken, accessToken) {

}