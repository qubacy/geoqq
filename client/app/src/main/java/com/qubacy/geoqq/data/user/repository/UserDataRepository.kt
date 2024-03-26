package com.qubacy.geoqq.data.user.repository

import com.qubacy.geoqq.data._common.repository.producing.ProducingDataRepository
import com.qubacy.geoqq.data._common.util.http.executor.executeNetworkRequest
import com.qubacy.geoqq.data.error.repository.ErrorDataRepository
import com.qubacy.geoqq.data.token.repository.TokenDataRepository
import com.qubacy.geoqq.data.user.model.toDataUser
import com.qubacy.geoqq.data.user.model.toUserEntity
import com.qubacy.geoqq.data.user.repository.result.GetUsersByIdsDataResult
import com.qubacy.geoqq.data.user.repository.source.http.HttpUserDataSource
import com.qubacy.geoqq.data.user.repository.source.http.request.GetUsersRequest
import com.qubacy.geoqq.data.user.repository.source.local.LocalUserDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

class UserDataRepository @Inject constructor(
    coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO,
    coroutineScope: CoroutineScope = CoroutineScope(coroutineDispatcher),
    val errorDataRepository: ErrorDataRepository,
    val tokenDataRepository: TokenDataRepository,
    val localUserDataSource: LocalUserDataSource,
    val httpUserDataSource: HttpUserDataSource
    // todo: add a websocket source..
) : ProducingDataRepository(coroutineDispatcher, coroutineScope) {
    suspend fun getUsersByIds(userIds: List<Long>) {
        val localUsers = localUserDataSource.getUsersByIds(userIds)

        if (localUsers != null) {
            val localDataUsers = localUsers.map { it.toDataUser() }

            mResultFlow.emit(GetUsersByIdsDataResult(localDataUsers))
        }

        val accessToken = tokenDataRepository.getTokens().accessToken

        val getUsersRequest = GetUsersRequest(accessToken, userIds)
        val getUsersCall = httpUserDataSource.getUsers(getUsersRequest)
        val getUsersResponse = executeNetworkRequest(errorDataRepository, getUsersCall)

        val httpDataUsers = getUsersResponse.users.map { it.toDataUser() }

        mResultFlow.emit(GetUsersByIdsDataResult(httpDataUsers))

        val usersToSave = httpDataUsers.map { it.toUserEntity() }

        localUserDataSource.saveUsers(usersToSave)
    }
}