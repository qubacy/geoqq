package com.qubacy.geoqq.data.token.repository.result

import com.auth0.android.jwt.Claim
import com.qubacy.geoqq.data.common.repository.common.result.common.Result

class GetAccessTokenPayloadResult(
    val payload: Map<String, Claim>
) : Result() {

}