package com.qubacy.geoqq.data.user.repository

import com.qubacy.geoqq.data.common.repository.common.result.common.Result
import com.qubacy.geoqq.data.common.repository.common.result.error.ErrorResult
import com.qubacy.geoqq.data.common.repository.common.result.interruption.InterruptionResult
import com.qubacy.geoqq.data.common.repository.common.source.network.error.NetworkDataSourceErrorEnum
import com.qubacy.geoqq.data.common.repository.common.source.network.model.response.common.Response
import com.qubacy.geoqq.data.common.repository.network.NetworkDataRepository
import com.qubacy.geoqq.data.token.repository.TokenDataRepository
import com.qubacy.geoqq.data.user.models.DataUser
import com.qubacy.geoqq.data.user.repository.result.GetUserByIdResult
import com.qubacy.geoqq.data.user.repository.source.local.LocalUserDataSource
import com.qubacy.geoqq.data.user.repository.source.network.NetworkUserDataSource
import com.qubacy.geoqq.data.user.repository.source.network.response.GetUserResponse
import retrofit2.Call
import java.io.IOException
import java.net.SocketException

class UserDataRepository(
    val tokenDataRepository: TokenDataRepository,
    val localUserDataSource: LocalUserDataSource,
    val networkUserDataSource: NetworkUserDataSource
) : NetworkDataRepository() {
    suspend fun getUserById(userId: Long): Result {
        // todo: getting user from the local storage..



        // todo: requesting the user's data using network:

        var response: retrofit2.Response<GetUserResponse>? = null

        try {
            mCurrentNetworkRequest = networkUserDataSource
                .getUser(userId) as Call<Response>
            response = mCurrentNetworkRequest!!
                .execute() as retrofit2.Response<GetUserResponse>

        } catch (e: SocketException) { return InterruptionResult()
        } catch (e: IOException) {
            return ErrorResult(NetworkDataSourceErrorEnum.UNKNOWN_NETWORK_FAILURE.error)
        }

        val error = retrieveNetworkError(response as retrofit2.Response<Response>)

        if (error != null) return ErrorResult(error)

        val responseBody = response.body()!!

        val user = DataUser(
            responseBody.username,
            responseBody.description,
            responseBody.avatar,
            responseBody.isMate
        )

        return GetUserByIdResult(user)
    }

}