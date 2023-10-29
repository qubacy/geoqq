package com.qubacy.geoqq.data.signin.repository

import com.qubacy.geoqq.data.signin.repository.result.SignInWithUsernamePasswordResult
import com.qubacy.geoqq.data.signin.repository.source.network.NetworkSignInDataSource
import com.qubacy.geoqq.data.common.repository.DataRepository
import com.qubacy.geoqq.data.common.repository.result.common.Result
import com.qubacy.geoqq.data.common.repository.result.error.ErrorResult
import com.qubacy.geoqq.data.common.repository.source.network.model.response.common.Response
import com.qubacy.geoqq.data.common.util.HasherUtil
import com.qubacy.geoqq.data.common.util.StringEncodingUtil
import com.qubacy.geoqq.data.signin.repository.result.SignInWithRefreshTokenResult

class SignInDataRepository(
    val networkSignInDataSource: NetworkSignInDataSource
) : DataRepository() {
    fun signInWithUsernamePassword(
        login: String,
        password: String
    ) : Result {
        val passwordHashBytes = HasherUtil.hashString(password, HasherUtil.HashAlgorithm.SHA256)
        val passwordHash = StringEncodingUtil.bytesAsBase64String(passwordHashBytes)

        val response = networkSignInDataSource
            .signInWithUsernameAndPassword(login, passwordHash).execute()

        val error = retrieveNetworkError(response as retrofit2.Response<Response>)

        if (error != null) return ErrorResult(error)

        val responseBody = response.body()!!

        val accessToken = responseBody.accessToken
        val refreshToken = responseBody.refreshToken

        return SignInWithUsernamePasswordResult(
            accessToken = accessToken, refreshToken = refreshToken)
    }

    fun signInWithRefreshToken(
        refreshToken: String
    ): Result {
        val response = networkSignInDataSource.signInWithRefreshToken(refreshToken).execute()

        val error = retrieveNetworkError(response as retrofit2.Response<Response>)

        if (error != null) return ErrorResult(error)

        val responseBody = response.body()!!

        val newAccessToken = responseBody.accessToken
        val newRefreshToken = responseBody.refreshToken

        return SignInWithRefreshTokenResult(
            accessToken = newAccessToken, refreshToken = newRefreshToken)
    }
}