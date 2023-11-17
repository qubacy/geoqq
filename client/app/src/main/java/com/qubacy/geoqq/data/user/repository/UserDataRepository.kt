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
import com.qubacy.geoqq.data.user.repository.result.GetUsersByIdsResult
import com.qubacy.geoqq.data.user.repository.result.GetUsersWithDatabaseResult
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
    private fun insertOrUpdateUserEntityWithDatabase(dataUser: DataUser): Result {
        val userEntity = UserEntity(
            dataUser.id,
            dataUser.username,
            dataUser.description,
            dataUser.avatarId,
            if (dataUser.isMate) 1 else 0
        )

        try {
            val gottenUserEntity = localUserDataSource.getUserById(userEntity.id)

            if (userEntity == gottenUserEntity)
                return InsertUserIntoDatabaseResult(false)

            if (gottenUserEntity == null) localUserDataSource.insertUser(userEntity)
            else localUserDataSource.updateUser(userEntity)

        } catch (e: Exception) {
            return ErrorResult(ErrorContext.Database.UNKNOWN_DATABASE_ERROR.id)
        }

        return InsertUserIntoDatabaseResult(true)
    }

    private suspend fun getUserWithNetworkAndSaveUpdated(
        userId: Long, accessToken: String
    ): Result {
        val networkCall = networkUserDataSource.getUser(userId, accessToken) as Call<Response>
        val executeNetworkRequestResult = executeNetworkRequest(networkCall)

        if (executeNetworkRequestResult is ErrorResult) return executeNetworkRequestResult
        if (executeNetworkRequestResult is InterruptionResult) return executeNetworkRequestResult

        val responseBody = (executeNetworkRequestResult as ExecuteNetworkRequestResult)
            .response as GetUserResponse

        val insertUserResult = insertOrUpdateUserEntityWithDatabase(responseBody.toDataUser())

        if (insertUserResult is ErrorResult) return insertUserResult
        if (insertUserResult is InterruptionResult) return insertUserResult

        val insertUserResultCast = insertUserResult as InsertUserIntoDatabaseResult

        return GetUserWithNetworkResult(
            responseBody.toDataUser(), insertUserResultCast.isUpdatedOrInserted)
    }

    private suspend fun getUserWithNetworkForUpdate(userId: Long, accessToken: String) {
        val getUserWithNetworkResult = getUserWithNetworkAndSaveUpdated(userId, accessToken)

        if (getUserWithNetworkResult is ErrorResult) return emitResult(getUserWithNetworkResult)
        if (getUserWithNetworkResult is InterruptionResult) return emitResult(getUserWithNetworkResult)

        val getUserWithNetworkResultCast = getUserWithNetworkResult as GetUserWithNetworkResult

        if (getUserWithNetworkResultCast.isNew)
            emitResult(GetUserByIdResult(getUserWithNetworkResultCast.user))
    }

    private suspend fun getUserWithNetworkForResult(userId: Long, accessToken: String): Result {
        return getUserWithNetworkAndSaveUpdated(userId, accessToken)
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

    private fun getUsersWithDatabase(usersIds: List<Long>): Result {
        val users = mutableListOf<DataUser>()

        for (userId in usersIds) {
            val getUserWithDatabaseResult = getUserWithDatabase(userId)

            if (getUserWithDatabaseResult is ErrorResult) return getUserWithDatabaseResult

            val user = (getUserWithDatabaseResult as GetUserWithDatabaseResult).user

            if (user != null) users.add(user)
        }

        return GetUsersWithDatabaseResult(users)
    }

    suspend fun getUserById(userId: Long, accessToken: String, isLatest: Boolean = true): Result {
        val getUserWithDatabaseResult = getUserWithDatabase(userId)

        if (getUserWithDatabaseResult is ErrorResult) return getUserWithDatabaseResult
        if (getUserWithDatabaseResult is InterruptionResult) return getUserWithDatabaseResult

        val getUserWithDatabaseResultCast = getUserWithDatabaseResult as GetUserWithDatabaseResult

        if (getUserWithDatabaseResultCast.user != null) {
            if (isLatest) getUserWithNetworkForUpdate(userId, accessToken)

            return GetUserByIdResult(getUserWithDatabaseResultCast.user)
        }

        val getUserWithNetworkResult = getUserWithNetworkForResult(userId, accessToken)

        if (getUserWithNetworkResult is ErrorResult) return getUserWithNetworkResult
        if (getUserWithNetworkResult is InterruptionResult) return getUserWithNetworkResult

        return GetUserByIdResult((getUserWithNetworkResult as GetUserWithNetworkResult).user)
    }

    suspend fun getUsersByIds(
        userIds: List<Long>,
        accessToken: String,
        isLatest: Boolean = true
    ): Result {
        val getUsersWithDatabaseResult = getUsersWithDatabase(userIds)

        if (getUsersWithDatabaseResult is ErrorResult) return getUsersWithDatabaseResult
        if (getUsersWithDatabaseResult is InterruptionResult) return getUsersWithDatabaseResult

        val getUsersWithDatabaseResultCast = getUsersWithDatabaseResult as GetUsersWithDatabaseResult

        if (getUsersWithDatabaseResultCast.dataUsers.size == userIds.size) {
            if (isLatest) {
                // todo: getting users from the network for update..
            }

            return GetUsersByIdsResult(getUsersWithDatabaseResultCast.dataUsers, true)
        }

        // todo: getting users from the network for result..

        return GetUsersByIdsResult(, false)
    }
}