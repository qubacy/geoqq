package com.qubacy.geoqq.data.token.repository.result

import com.qubacy.geoqq.data.common.repository.common.result.common.Result

data class GetAccessTokenResult(
    val accessToken: String
) : Result() {

}