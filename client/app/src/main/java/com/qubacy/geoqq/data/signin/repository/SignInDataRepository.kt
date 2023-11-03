package com.qubacy.geoqq.data.signin.repository

import com.qubacy.geoqq.data.signin.repository.result.SignInWithLoginPasswordResult
import com.qubacy.geoqq.data.signin.repository.source.network.NetworkSignInDataSource
import com.qubacy.geoqq.data.common.repository.common.result.common.Result
import com.qubacy.geoqq.data.common.repository.common.result.error.ErrorResult
import com.qubacy.geoqq.data.common.repository.common.result.interruption.InterruptionResult
import com.qubacy.geoqq.data.common.repository.common.source.network.model.response.common.Response
import com.qubacy.geoqq.data.common.repository.network.NetworkDataRepository
import com.qubacy.geoqq.data.common.repository.network.result.ExecuteNetworkRequestResult
import com.qubacy.geoqq.data.common.util.HasherUtil
import com.qubacy.geoqq.data.common.util.StringEncodingDecodingUtil
import com.qubacy.geoqq.data.signin.repository.source.network.model.response.SignInWithLoginPasswordResponse
import retrofit2.Call

class SignInDataRepository(
    val networkSignInDataSource: NetworkSignInDataSource
) : NetworkDataRepository() {
    suspend fun signInWithLoginPassword(
        login: String,
        password: String
    ) : Result {
        val passwordHashBytes = HasherUtil.hashString(password, HasherUtil.HashAlgorithm.SHA256)
        val passwordHash = StringEncodingDecodingUtil.bytesAsBase64String(passwordHashBytes)

        val networkCall = networkSignInDataSource
            .signInWithUsernameAndPassword(login, passwordHash) as Call<Response>
        val executeNetworkRequestResult = executeNetworkRequest(networkCall)

        if (executeNetworkRequestResult is ErrorResult) return executeNetworkRequestResult
        if (executeNetworkRequestResult is InterruptionResult) return executeNetworkRequestResult

        val responseBody = (executeNetworkRequestResult as ExecuteNetworkRequestResult)
            .response as SignInWithLoginPasswordResponse

        val accessToken = responseBody.accessToken
        val refreshToken = responseBody.refreshToken

        return SignInWithLoginPasswordResult(
            accessToken = accessToken, refreshToken = refreshToken)
    }
}