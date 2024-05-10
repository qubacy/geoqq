package com.qubacy.geoqq.data.auth.repository.source.http

import com.qubacy.geoqq.data._common.repository._common.source.remote.http._common.executor.HttpCallExecutor
import com.qubacy.geoqq.data.auth.repository.source.http.api.HttpAuthDataSourceApi
import com.qubacy.geoqq.data.auth.repository.source.http.api.response.SignInResponse
import com.qubacy.geoqq.data.auth.repository.source.http.api.response.SignUpResponse
import javax.inject.Inject

open class HttpAuthDataSource @Inject constructor(
    private val mHttpAuthDataSourceApi: HttpAuthDataSourceApi,
    private val mHttpCallExecutor: HttpCallExecutor
) {
    open fun signIn(
        login: String,
        passwordHash: String
    ): SignInResponse {
        val signInRequest = mHttpAuthDataSourceApi.signIn(login, passwordHash)
        val signInResponse = mHttpCallExecutor.executeNetworkRequest(signInRequest)

        return signInResponse
    }

    open fun signUp(
        login: String,
        passwordHash: String
    ): SignUpResponse {
        val signUpRequest = mHttpAuthDataSourceApi.signUp(login, passwordHash)
        val signUpResponse = mHttpCallExecutor.executeNetworkRequest(signUpRequest)

        return signUpResponse
    }
}