package com.qubacy.geoqq.data.signup.repository

import com.qubacy.geoqq.data.common.repository.common.result.common.Result
import com.qubacy.geoqq.data.common.repository.common.result.error.ErrorResult
import com.qubacy.geoqq.data.common.repository.common.result.interruption.InterruptionResult
import com.qubacy.geoqq.data.common.repository.common.source.network.model.response.common.Response
import com.qubacy.geoqq.data.common.repository.network.NetworkDataRepository
import com.qubacy.geoqq.data.common.repository.network.result.ExecuteNetworkRequestResult
import com.qubacy.geoqq.data.common.util.HasherUtil
import com.qubacy.geoqq.data.common.util.StringEncodingDecodingUtil
import com.qubacy.geoqq.data.signup.repository.result.SignUpResult
import com.qubacy.geoqq.data.signup.repository.source.network.NetworkSignUpDataSource
import com.qubacy.geoqq.data.signup.repository.source.network.response.SignUpResponse
import retrofit2.Call

class SignUpDataRepository(
    val networkSignUpDataSource: NetworkSignUpDataSource
) : NetworkDataRepository() {
    suspend fun signUp(login: String, password: String): Result {
        val passwordHashBytes = HasherUtil.hashString(password, HasherUtil.HashAlgorithm.SHA256)
        val passwordHash = StringEncodingDecodingUtil.bytesAsBase64String(passwordHashBytes)

        val networkCall = networkSignUpDataSource
            .signUp(login, passwordHash) as Call<Response>
        val executeNetworkRequestResult = executeNetworkRequest(networkCall)

        if (executeNetworkRequestResult is ErrorResult) return executeNetworkRequestResult
        if (executeNetworkRequestResult is InterruptionResult) return executeNetworkRequestResult

        val responseBody = (executeNetworkRequestResult as ExecuteNetworkRequestResult)
            .response as SignUpResponse

        val accessToken = responseBody.accessToken
        val refreshToken = responseBody.refreshToken

        return SignUpResult(refreshToken, accessToken)
    }
}