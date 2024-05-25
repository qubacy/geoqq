package com.qubacy.geoqq.data.auth.repository._common.source.remote.http.rest.impl

import com.qubacy.geoqq.data._common.repository._common.source.remote.http._common.executor.impl.HttpCallExecutorImpl
import com.qubacy.geoqq.data.auth.repository._common.source.remote.http.rest._common.RemoteAuthHttpRestDataSource
import com.qubacy.geoqq.data.auth.repository._common.source.remote.http.rest._common.api.RemoteAuthHttpRestDataSourceApi
import com.qubacy.geoqq.data.auth.repository._common.source.remote.http.rest._common.api.response.SignInResponse
import com.qubacy.geoqq.data.auth.repository._common.source.remote.http.rest._common.api.response.SignUpResponse
import javax.inject.Inject

open class RemoteAuthHttpRestDataSourceImpl @Inject constructor(
    private val mRemoteAuthHttpRestDataSourceApi: RemoteAuthHttpRestDataSourceApi,
    private val mHttpCallExecutor: HttpCallExecutorImpl
) : RemoteAuthHttpRestDataSource {
    override fun signIn(
        login: String,
        passwordHash: String
    ): SignInResponse {
        val signInRequest = mRemoteAuthHttpRestDataSourceApi.signIn(login, passwordHash)
        val signInResponse = mHttpCallExecutor.executeNetworkRequest(signInRequest)

        return signInResponse
    }

    override fun signUp(
        login: String,
        passwordHash: String
    ): SignUpResponse {
        val signUpRequest = mRemoteAuthHttpRestDataSourceApi.signUp(login, passwordHash)
        val signUpResponse = mHttpCallExecutor.executeNetworkRequest(signUpRequest)

        return signUpResponse
    }
}