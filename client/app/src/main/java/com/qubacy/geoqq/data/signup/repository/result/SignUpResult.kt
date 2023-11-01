package com.qubacy.geoqq.data.signup.repository.result

import com.qubacy.geoqq.data.common.repository.common.result.common.Result

class SignUpResult(
    val refreshToken: String,
    val accessToken: String
) : Result() {

}