package com.qubacy.geoqq.data.signin.repository

import com.qubacy.geoqq.data.signin.repository.result.SignInWithUsernamePasswordResult
import com.qubacy.geoqq.data.signin.repository.source.network.NetworkSignInDataSource
import com.qubacy.geoqq.data.common.repository.DataRepository
import com.qubacy.geoqq.data.common.repository.result.common.Result
import com.qubacy.geoqq.data.common.repository.result.error.ErrorResult
import com.qubacy.geoqq.data.common.repository.source.network.error.NetworkDataSourceErrorEnum
import com.qubacy.geoqq.data.common.repository.source.network.model.response.common.Response
import com.qubacy.geoqq.data.common.util.HasherUtil
import com.qubacy.geoqq.data.common.util.StringEncodingUtil
import com.qubacy.geoqq.data.signin.repository.source.network.model.response.SignInWithUsernamePasswordResponse
import com.qubacy.geoqq.data.token.repository.TokenDataRepository
import java.io.IOException

class SignInDataRepository(
    val tokenDataRepository: TokenDataRepository,
    val networkSignInDataSource: NetworkSignInDataSource
) : DataRepository() {
    suspend fun signInWithUsernamePassword(
        login: String,
        password: String
    ) : Result {
        val passwordHashBytes = HasherUtil.hashString(password, HasherUtil.HashAlgorithm.SHA256)
        val passwordHash = StringEncodingUtil.bytesAsBase64String(passwordHashBytes)

        var response: retrofit2.Response<SignInWithUsernamePasswordResponse>? = null

        try {
            response = networkSignInDataSource
                    .signInWithUsernameAndPassword(login, passwordHash).execute()

        } catch (e: IOException) {
            return ErrorResult(NetworkDataSourceErrorEnum.UNKNOWN_NETWORK_FAILURE.error)
        }

        val error = retrieveNetworkError(response as retrofit2.Response<Response>)

        if (error != null) return ErrorResult(error)

        val responseBody = response.body()!!

        val accessToken = responseBody.accessToken
        val refreshToken = responseBody.refreshToken

        val saveTokensResult = tokenDataRepository.saveTokens(refreshToken, accessToken)

        if (saveTokensResult is ErrorResult) return saveTokensResult

        return SignInWithUsernamePasswordResult(
            accessToken = accessToken, refreshToken = refreshToken)
    }

    suspend fun signInWithRefreshToken(): Result {
        return tokenDataRepository.updateTokens()
    }
}