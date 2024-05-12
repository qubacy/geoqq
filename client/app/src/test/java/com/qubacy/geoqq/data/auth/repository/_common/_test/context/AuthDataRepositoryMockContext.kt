package com.qubacy.geoqq.data.auth.repository._common._test.context

import com.auth0.android.jwt.Claim
import com.auth0.android.jwt.JWT
import com.qubacy.geoqq._common._test.util.mock.Base64MockUtil
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.rest.token._common.api.response.UpdateTokensResponse
import com.qubacy.geoqq.data.auth.repository._common.source.remote.http.rest._common.api.response.SignInResponse
import com.qubacy.geoqq.data.auth.repository._common.source.remote.http.rest._common.api.response.SignUpResponse

object AuthDataRepositoryMockContext {
    const val DEFAULT_VALID_TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9." +
        "eyJpYXQiOjE1MTYyMzkwMjIsImV4cCI6MzUxNjIzOTAyMn0." +
        "hvYjFTg5JYV1AIoP1cMLWSScRrFhr7lFwYow4eVQGTc"
    val DEFAULT_VALID_TOKEN_PAYLOAD: Map<String, Claim>

    val DEFAULT_UPDATE_TOKENS_RESPONSE = UpdateTokensResponse(
        "sign in accessToken",
        "sign in refreshToken"
    )
    val DEFAULT_SIGN_IN_RESPONSE = SignInResponse(
        "sign in accessToken",
        "sign in refreshToken"
    )
    val DEFAULT_SIGN_UP_RESPONSE = SignUpResponse(
        "sign up accessToken",
        "sign up refreshToken"
    )

    init {
        Base64MockUtil.mockBase64()

        DEFAULT_VALID_TOKEN_PAYLOAD = JWT(DEFAULT_VALID_TOKEN).claims
    }
}