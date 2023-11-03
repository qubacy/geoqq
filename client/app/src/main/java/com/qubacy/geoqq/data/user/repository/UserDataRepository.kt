package com.qubacy.geoqq.data.user.repository

import com.qubacy.geoqq.common.error.ErrorContext
import com.qubacy.geoqq.data.common.repository.common.result.common.Result
import com.qubacy.geoqq.data.common.repository.common.result.error.ErrorResult
import com.qubacy.geoqq.data.common.repository.common.result.interruption.InterruptionResult
import com.qubacy.geoqq.data.common.repository.common.source.network.model.response.common.Response
import com.qubacy.geoqq.data.common.repository.network.NetworkDataRepository
import com.qubacy.geoqq.data.common.repository.network.result.ExecuteNetworkRequestResult
import com.qubacy.geoqq.data.user.model.DataUser
import com.qubacy.geoqq.data.user.repository.result.GetNetworkWithNetworkResult
import com.qubacy.geoqq.data.user.repository.result.GetUserByIdResult
import com.qubacy.geoqq.data.user.repository.result.GetUserWithDatabaseResult
import com.qubacy.geoqq.data.user.repository.source.local.LocalUserDataSource
import com.qubacy.geoqq.data.user.repository.source.local.entity.UserEntity
import com.qubacy.geoqq.data.user.repository.source.local.entity.toDataUser
import com.qubacy.geoqq.data.user.repository.source.network.NetworkUserDataSource
import com.qubacy.geoqq.data.user.repository.source.network.response.GetUserResponse
import retrofit2.Call

class UserDataRepository(
    val localUserDataSource: LocalUserDataSource,
    val networkUserDataSource: NetworkUserDataSource
) : NetworkDataRepository() {
    private fun getUserWithNetwork(userId: Long): Result {
        val networkCall = networkUserDataSource.getUser(userId) as Call<Response>
        val executeNetworkRequestResult = executeNetworkRequest(networkCall)

        if (executeNetworkRequestResult is ErrorResult) return executeNetworkRequestResult
        if (executeNetworkRequestResult is InterruptionResult) return executeNetworkRequestResult

        val responseBody = (executeNetworkRequestResult as ExecuteNetworkRequestResult)
            .response as GetUserResponse

        val user = DataUser(
            responseBody.username,
            responseBody.description,
            responseBody.avatarId,
            responseBody.isMate
        )

        return GetNetworkWithNetworkResult(user)
    }

    private fun getUserWithDatabase(userId: Long): Result {
        var userEntity: UserEntity? = null

        try {
            userEntity = localUserDataSource.getUserById(userId)

        } catch (e: Exception) {
            return ErrorResult(ErrorContext.Database.UNKNOWN_DATABASE_ERROR.id)
        }

        return GetUserWithDatabaseResult(userEntity?.toDataUser())
    }

    suspend fun getUserById(userId: Long): Result {
        val getUserWithDatabaseResult = getUserWithDatabase(userId)

        if (getUserWithDatabaseResult is ErrorResult) return getUserWithDatabaseResult
        if (getUserWithDatabaseResult is InterruptionResult) return getUserWithDatabaseResult

        val getUserWithDatabaseResultCast = getUserWithDatabaseResult as GetUserWithDatabaseResult

        if (getUserWithDatabaseResultCast.user != null)
            return GetUserByIdResult(getUserWithDatabaseResultCast.user)

        val getUserWithNetworkResult = getUserWithNetwork(userId)

        if (getUserWithNetworkResult is ErrorResult) return getUserWithNetworkResult
        if (getUserWithNetworkResult is InterruptionResult) return getUserWithNetworkResult

        return GetUserByIdResult((getUserWithNetworkResult as GetNetworkWithNetworkResult).user)
    }

}