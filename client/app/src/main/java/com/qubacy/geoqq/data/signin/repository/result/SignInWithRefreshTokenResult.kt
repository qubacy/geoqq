package com.qubacy.geoqq.data.signin.repository.result

import com.qubacy.geoqq.data.common.repository.common.result.common.Result

class SignInWithRefreshTokenResult(
    val refreshToken: String,
    val accessToken: String
) : Result() {

}