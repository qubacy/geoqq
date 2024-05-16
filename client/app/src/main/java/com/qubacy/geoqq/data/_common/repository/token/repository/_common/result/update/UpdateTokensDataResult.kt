package com.qubacy.geoqq.data._common.repository.token.repository._common.result.update

import com.qubacy.geoqq.data._common.repository.token.repository._common.result._common.TokensDataResult

class UpdateTokensDataResult(
    refreshToken: String,
    accessToken: String
) : TokensDataResult(refreshToken, accessToken) {

}