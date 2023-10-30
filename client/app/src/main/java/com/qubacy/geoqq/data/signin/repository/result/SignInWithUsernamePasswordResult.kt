package com.qubacy.geoqq.data.signin.repository.result

import com.qubacy.geoqq.data.common.repository.common.result.common.Result

class SignInWithUsernamePasswordResult(
    val accessToken: String,
    val refreshToken: String
) : Result() {

}