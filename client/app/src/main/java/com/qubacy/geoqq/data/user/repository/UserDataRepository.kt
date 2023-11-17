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
import com.qubacy.geoqq.data.user.repository.result.GetUsersWithNetworkResult
import com.qubacy.geoqq.data.user.repository.result.GetUserWithDatabaseResult
import com.qubacy.geoqq.data.user.repository.result.GetUsersByIdsResult
import com.qubacy.geoqq.data.user.repository.result.GetUsersWithDatabaseResult
import com.qubacy.geoqq.data.user.repository.result.InsertUsersIntoDatabaseResult
import com.qubacy.geoqq.data.user.repository.source.local.LocalUserDataSource
import com.qubacy.geoqq.data.user.repository.source.local.entity.UserEntity
import com.qubacy.geoqq.data.user.repository.source.local.entity.toDataUser
import com.qubacy.geoqq.data.user.repository.source.network.NetworkUserDataSource
import com.qubacy.geoqq.data.user.repository.source.network.response.GetUsersResponse
import com.qubacy.geoqq.data.user.repository.source.network.response.toDataUser
import retrofit2.Call

class UserDataRepository(
    val localUserDataSource: LocalUserDataSource,
    val networkUserDataSource: NetworkUserDataSource
) : FlowableDataRepository() {
    private fun insertOrUpdateUserEntitiesWithDatabase(dataUsers: List<DataUser>): Result {
        try {
            // todo: make a new query which with insert MULTIPLE USER ENTITIES AT A TIME!!!

            for (dataUser in dataUsers) {
                val userEntity = UserEntity(
                    dataUser.id,
                    dataUser.username,
                    dataUser.description,
                    dataUser.avatarId,
                    if (dataUser.isMate) 1 else 0
                )

                val gottenUserEntity = localUserDataSource.getUserById(userEntity.id)

                if (userEntity == gottenUserEntity)
                    return InsertUsersIntoDatabaseResult(false)

                if (gottenUserEntity == null) localUserDataSource.insertUser(userEntity)
                else localUserDataSource.updateUser(userEntity)
            }

        } catch (e: Exception) {
            return ErrorResult(ErrorContext.Database.UNKNOWN_DATABASE_ERROR.id)
        }

        return InsertUsersIntoDatabaseResult(true)
    }

    private suspend fun getUsersWithNetworkAndSaveUpdated(
        usersIds: List<Long>, accessToken: String
    ): Result {
        val networkCall = networkUserDataSource.getUsers(usersIds, accessToken) as Call<Response>
        val executeNetworkRequestResult = executeNetworkRequest(networkCall)

        if (executeNetworkRequestResult is ErrorResult) return executeNetworkRequestResult
        if (executeNetworkRequestResult is InterruptionResult) return executeNetworkRequestResult

        val responseBody = (executeNetworkRequestResult as ExecuteNetworkRequestResult)
            .response as GetUsersResponse
        val gottenUsers = responseBody.users.map { it.toDataUser() }

        val insertUsersResult =
            insertOrUpdateUserEntitiesWithDatabase(gottenUsers)

        if (insertUsersResult is ErrorResult) return insertUsersResult
        if (insertUsersResult is InterruptionResult) return insertUsersResult

        val insertUsersResultCast = insertUsersResult as InsertUsersIntoDatabaseResult

        return GetUsersWithNetworkResult(gottenUsers, insertUsersResult.areUpdatedOrInserted)
    }

    private suspend fun getUsersWithNetworkForUpdate(usersIds: List<Long>, accessToken: String) {
        val getUserWithNetworkResult = getUsersWithNetworkAndSaveUpdated(usersIds, accessToken)

        if (getUserWithNetworkResult is ErrorResult) return emitResult(getUserWithNetworkResult)
        if (getUserWithNetworkResult is InterruptionResult) return emitResult(getUserWithNetworkResult)

        val getUserWithNetworkResultCast = getUserWithNetworkResult as GetUsersWithNetworkResult

        if (getUserWithNetworkResultCast.areNew) {
            if (usersIds.size == 1)
                emitResult(GetUserByIdResult(getUserWithNetworkResultCast.users.first()))
            else
                emitResult(GetUsersByIdsResult(
                    getUserWithNetworkResultCast.users, getUserWithNetworkResultCast.areNew)
                )
        }
    }

    private suspend fun getUsersWithNetworkForResult(usersIds: List<Long>, accessToken: String): Result {
        return getUsersWithNetworkAndSaveUpdated(usersIds, accessToken)
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
            if (isLatest) getUsersWithNetworkForUpdate(listOf(userId), accessToken)

            return GetUserByIdResult(getUserWithDatabaseResultCast.user)
        }

        val getUserWithNetworkResult = getUsersWithNetworkForResult(listOf(userId), accessToken)

        if (getUserWithNetworkResult is ErrorResult) return getUserWithNetworkResult
        if (getUserWithNetworkResult is InterruptionResult) return getUserWithNetworkResult

        return GetUserByIdResult((getUserWithNetworkResult as GetUsersWithNetworkResult).users.first())
    }

    suspend fun getUsersByIds(
        usersIds: List<Long>,
        accessToken: String,
        isLatest: Boolean = true
    ): Result {
        val getUsersWithDatabaseResult = getUsersWithDatabase(usersIds)

        if (getUsersWithDatabaseResult is ErrorResult) return getUsersWithDatabaseResult
        if (getUsersWithDatabaseResult is InterruptionResult) return getUsersWithDatabaseResult

        val getUsersWithDatabaseResultCast = getUsersWithDatabaseResult as GetUsersWithDatabaseResult

        if (getUsersWithDatabaseResultCast.dataUsers.size == usersIds.size) {
            if (isLatest) getUsersWithNetworkForUpdate(usersIds, accessToken)

            return GetUsersByIdsResult(getUsersWithDatabaseResultCast.dataUsers, true)
        }

        val getUsersWithNetworkResult = getUsersWithNetworkForResult(usersIds, accessToken)

        if (getUsersWithNetworkResult is ErrorResult) return getUsersWithNetworkResult
        if (getUsersWithNetworkResult is InterruptionResult) return getUsersWithNetworkResult

        val getUsersWithNetworkResultCast = getUsersWithNetworkResult as GetUsersWithNetworkResult

        return GetUsersByIdsResult(
            getUsersWithNetworkResultCast.users, getUsersWithNetworkResultCast.areNew)
    }
}