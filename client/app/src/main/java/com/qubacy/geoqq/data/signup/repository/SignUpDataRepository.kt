package com.qubacy.geoqq.data.signup.repository

import com.qubacy.geoqq.data.common.repository.common.result.common.Result
import com.qubacy.geoqq.data.common.repository.common.result.error.ErrorResult
import com.qubacy.geoqq.data.common.repository.common.result.interruption.InterruptionResult
import com.qubacy.geoqq.data.common.repository.common.source.network.error.NetworkDataSourceErrorEnum
import com.qubacy.geoqq.data.common.repository.common.source.network.model.response.common.Response
import com.qubacy.geoqq.data.common.repository.network.NetworkDataRepository
import com.qubacy.geoqq.data.common.util.HasherUtil
import com.qubacy.geoqq.data.common.util.StringEncodingDecodingUtil
import com.qubacy.geoqq.data.signup.repository.result.SignUpResult
import com.qubacy.geoqq.data.signup.repository.source.network.NetworkSignUpDataSource
import com.qubacy.geoqq.data.signup.repository.source.network.response.SignUpResponse
import com.qubacy.geoqq.data.token.repository.TokenDataRepository
import retrofit2.Call
import java.io.IOException
import java.net.SocketException

class SignUpDataRepository(
    val tokenDataRepository: TokenDataRepository,
    val networkSignUpDataSource: NetworkSignUpDataSource
) : NetworkDataRepository() {
    suspend fun signUp(login: String, password: String): Result {
        val passwordHashBytes = HasherUtil.hashString(password, HasherUtil.HashAlgorithm.SHA256)
        val passwordHash = StringEncodingDecodingUtil.bytesAsBase64String(passwordHashBytes)

        var response: retrofit2.Response<SignUpResponse>? = null

        try {
            mCurrentNetworkRequest = networkSignUpDataSource
                .signUp(login, passwordHash) as Call<Response>
            response = mCurrentNetworkRequest!!
                .execute() as retrofit2.Response<SignUpResponse>

        } catch (e: SocketException) { return InterruptionResult()
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

        return SignUpResult()
    }
}