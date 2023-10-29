package com.qubacy.geoqq.data.signin.repository.result

import com.qubacy.geoqq.data.common.repository.result.common.Result

class SignInWithRefreshTokenResult(
    val refreshToken: String,
    val accessToken: String
) : Result() {

}