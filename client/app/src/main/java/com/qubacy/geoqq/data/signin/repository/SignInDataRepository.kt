package com.qubacy.geoqq.data.signin.repository

import com.qubacy.geoqq.data.signin.repository.result.SignInWithLoginPasswordResult
import com.qubacy.geoqq.data.signin.repository.source.network.NetworkSignInDataSource
import com.qubacy.geoqq.data.common.repository.common.result.common.Result
import com.qubacy.geoqq.data.common.repository.common.result.error.ErrorResult
import com.qubacy.geoqq.data.common.repository.common.result.interruption.InterruptionResult
import com.qubacy.geoqq.data.common.repository.common.source.network.error.NetworkDataSourceErrorEnum
import com.qubacy.geoqq.data.common.repository.common.source.network.model.response.common.Response
import com.qubacy.geoqq.data.common.repository.network.NetworkDataRepository
import com.qubacy.geoqq.data.common.util.HasherUtil
import com.qubacy.geoqq.data.common.util.StringEncodingDecodingUtil
import com.qubacy.geoqq.data.signin.repository.source.network.model.response.SignInWithLoginPasswordResponse
import retrofit2.Call
import java.io.IOException
import java.net.SocketException

class SignInDataRepository(
    val networkSignInDataSource: NetworkSignInDataSource
) : NetworkDataRepository() {
    suspend fun signInWithLoginPassword(
        login: String,
        password: String
    ) : Result {
        val passwordHashBytes = HasherUtil.hashString(password, HasherUtil.HashAlgorithm.SHA256)
        val passwordHash = StringEncodingDecodingUtil.bytesAsBase64String(passwordHashBytes)

        var response: retrofit2.Response<SignInWithLoginPasswordResponse>? = null

        try {
            mCurrentNetworkRequest = networkSignInDataSource
                .signInWithUsernameAndPassword(login, passwordHash) as Call<Response>
            response = mCurrentNetworkRequest!!
                .execute() as retrofit2.Response<SignInWithLoginPasswordResponse>

        } catch (e: SocketException) { return InterruptionResult()
        } catch (e: IOException) {
            return ErrorResult(NetworkDataSourceErrorEnum.UNKNOWN_NETWORK_FAILURE.error)
        }

        val error = retrieveNetworkError(response as retrofit2.Response<Response>)

        if (error != null) return ErrorResult(error)

        val responseBody = response.body()!!

        val accessToken = responseBody.accessToken
        val refreshToken = responseBody.refreshToken

        return SignInWithLoginPasswordResult(
            accessToken = accessToken, refreshToken = refreshToken)
    }
}