package com.qubacy.geoqq.data.user.repository

import com.qubacy.geoqq.common.error.ErrorContext
import com.qubacy.geoqq.data.common.repository.common.result.common.Result
import com.qubacy.geoqq.data.common.repository.common.result.error.ErrorResult
import com.qubacy.geoqq.data.common.repository.common.result.interruption.InterruptionResult
import com.qubacy.geoqq.data.common.repository.common.source.network.model.response.common.Response
import com.qubacy.geoqq.data.common.repository.network.common.result.ExecuteNetworkRequestResult
import com.qubacy.geoqq.data.common.repository.network.flowable.FlowableDataRepository
import com.qubacy.geoqq.data.user.model.DataUser
import com.qubacy.geoqq.data.user.repository.result.GetUserByIdResult
import com.qubacy.geoqq.data.user.repository.result.GetUserWithNetworkResult
import com.qubacy.geoqq.data.user.repository.result.GetUserWithDatabaseResult
import com.qubacy.geoqq.data.user.repository.result.InsertUserIntoDatabaseResult
import com.qubacy.geoqq.data.user.repository.source.local.LocalUserDataSource
import com.qubacy.geoqq.data.user.repository.source.local.entity.UserEntity
import com.qubacy.geoqq.data.user.repository.source.local.entity.toDataUser
import com.qubacy.geoqq.data.user.repository.source.network.NetworkUserDataSource
import com.qubacy.geoqq.data.user.repository.source.network.response.GetUserResponse
import com.qubacy.geoqq.data.user.repository.source.network.response.toDataUser
import retrofit2.Call

class UserDataRepository(
    val localUserDataSource: LocalUserDataSource,
    val networkUserDataSource: NetworkUserDataSource
) : FlowableDataRepository() {
    private fun insertUserIntoDatabase(dataUser: DataUser): Result {
        val userEntity = UserEntity(
            dataUser.id,
            dataUser.username,
            dataUser.description,
            dataUser.avatarId,
            if (dataUser.isMate) 1 else 0
        )

        try {
            localUserDataSource.insertUser(userEntity)

        } catch (e: Exception) {
            return ErrorResult(ErrorContext.Database.UNKNOWN_DATABASE_ERROR.id)
        }

        return InsertUserIntoDatabaseResult()
    }

    private suspend fun getUserWithNetwork(
        userId: Long, accessToken: String, isForResult: Boolean
    ): Any {
        val networkCall = networkUserDataSource.getUser(userId, accessToken) as Call<Response>
        val executeNetworkRequestResult = executeNetworkRequest(networkCall)

        if (executeNetworkRequestResult is ErrorResult) {
            return if (isForResult) executeNetworkRequestResult
            else emitResult(executeNetworkRequestResult)
        }
        if (executeNetworkRequestResult is InterruptionResult) {
            return if (isForResult) executeNetworkRequestResult
            else emitResult(executeNetworkRequestResult)
        }

        val responseBody = (executeNetworkRequestResult as ExecuteNetworkRequestResult)
            .response as GetUserResponse

        val insertUserResult = insertUserIntoDatabase(responseBody.toDataUser())

        if (insertUserResult is ErrorResult) {
            return if (isForResult) insertUserResult
            else emitResult(insertUserResult)
        }
        if (insertUserResult is InterruptionResult) {
            return if (isForResult) insertUserResult
            else emitResult(insertUserResult)
        }

        return if (isForResult) GetUserWithNetworkResult(responseBody.toDataUser())
        else emitResult(GetUserByIdResult(responseBody.toDataUser()))
    }

    private suspend fun getUserWithNetworkForUpdate(userId: Long, accessToken: String) {
        getUserWithNetwork(userId, accessToken, false)
    }

    private suspend fun getUserWithNetworkForResult(userId: Long, accessToken: String): Result {
        return getUserWithNetwork(userId, accessToken, true) as Result
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

    suspend fun getUserById(userId: Long, accessToken: String): Result {
        val getUserWithDatabaseResult = getUserWithDatabase(userId)

        if (getUserWithDatabaseResult is ErrorResult) return getUserWithDatabaseResult
        if (getUserWithDatabaseResult is InterruptionResult) return getUserWithDatabaseResult

        val getUserWithDatabaseResultCast = getUserWithDatabaseResult as GetUserWithDatabaseResult

        if (getUserWithDatabaseResultCast.user != null) {
            getUserWithNetworkForUpdate(userId, accessToken)

            return GetUserByIdResult(getUserWithDatabaseResultCast.user)
        }

        val getUserWithNetworkResult = getUserWithNetworkForResult(userId, accessToken)

        if (getUserWithNetworkResult is ErrorResult) return getUserWithNetworkResult
        if (getUserWithNetworkResult is InterruptionResult) return getUserWithNetworkResult

        return GetUserByIdResult((getUserWithNetworkResult as GetUserWithNetworkResult).user)
    }
}