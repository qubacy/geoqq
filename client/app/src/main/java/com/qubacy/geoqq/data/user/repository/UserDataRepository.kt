package com.qubacy.geoqq.data.user.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.qubacy.geoqq._common.exception.error.ErrorAppException
import com.qubacy.geoqq._common.util.livedata.extension.await
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.executor.HttpCallExecutor
import com.qubacy.geoqq.data._common.repository.producing.ProducingDataRepository
import com.qubacy.geoqq.data.error.repository.ErrorDataRepository
import com.qubacy.geoqq.data.image.model.DataImage
import com.qubacy.geoqq.data.image.repository.ImageDataRepository
import com.qubacy.geoqq.data.token.error.type.DataTokenErrorType
import com.qubacy.geoqq.data.token.repository.TokenDataRepository
import com.qubacy.geoqq.data.user.model.DataUser
import com.qubacy.geoqq.data.user.model.toDataUser
import com.qubacy.geoqq.data.user.model.toUserEntity
import com.qubacy.geoqq.data.user.repository.result.GetUsersByIdsDataResult
import com.qubacy.geoqq.data.user.repository.source.http.HttpUserDataSource
import com.qubacy.geoqq.data.user.repository.source.http.request.GetUsersRequest
import com.qubacy.geoqq.data.user.repository.source.http.response.GetUsersResponse
import com.qubacy.geoqq.data.user.repository.source.local.LocalUserDataSource
import com.qubacy.geoqq.data.user.repository.source.local.entity.UserEntity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import kotlin.coroutines.coroutineContext

class UserDataRepository @Inject constructor(
    coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO,
    coroutineScope: CoroutineScope = CoroutineScope(coroutineDispatcher),
    private val mErrorDataRepository: ErrorDataRepository,
    private val mTokenDataRepository: TokenDataRepository,
    private val mImageDataRepository: ImageDataRepository,
    private val mLocalUserDataSource: LocalUserDataSource,
    private val mHttpUserDataSource: HttpUserDataSource,
    private val mHttpCallExecutor: HttpCallExecutor
    // todo: add a websocket source..
) : ProducingDataRepository(coroutineDispatcher, coroutineScope) {
    companion object {
        const val ACCESS_TOKEN_USER_ID_PAYLOAD_PROP_NAME = "user-id"
    }

    suspend fun getUsersByIds(userIds: List<Long>): LiveData<GetUsersByIdsDataResult> {
        val resultLiveData = MutableLiveData<GetUsersByIdsDataResult>()

        CoroutineScope(coroutineContext).launch {
            val localUsers = mLocalUserDataSource.getUsersByIds(userIds)
            val localDataUsers = localUsers?.let { resolveUserEntities(it) }

            if (localUsers != null)
                resultLiveData.postValue(GetUsersByIdsDataResult(localDataUsers!!))

            val accessToken = mTokenDataRepository.getTokens().accessToken

            val getUsersRequest = GetUsersRequest(accessToken, userIds)
            val getUsersCall = mHttpUserDataSource.getUsers(getUsersRequest)
            val getUsersResponse = mHttpCallExecutor.executeNetworkRequest(getUsersCall)

            val httpDataUsers = resolveGetUserResponses(getUsersResponse)

            if (localDataUsers?.containsAll(httpDataUsers) == true) return@launch

            if (localUsers == null) resultLiveData.postValue(GetUsersByIdsDataResult(httpDataUsers))
            else mResultFlow.emit(GetUsersByIdsDataResult(httpDataUsers))

            val usersToSave = httpDataUsers.map { it.toUserEntity() }

            mLocalUserDataSource.saveUsers(usersToSave)
        }

        return resultLiveData
    }

    suspend fun resolveUsers(userIds: List<Long>): Map<Long, DataUser> {
        val getUsersByIdsLiveData = getUsersByIds(userIds)
        val getUsersByIdsResult = getUsersByIdsLiveData.await()

        return getUsersByIdsResult.users.associateBy { it.id }
    }

    suspend fun resolveLocalUser(): DataUser {
        val localUserId = getLocalUserId()

        return getUsersByIds(listOf(localUserId)).await().users.first()
    }

    suspend fun resolveUsersWithLocalUser(userIds: List<Long>): Map<Long, DataUser> {
        val localUserId = getLocalUserId()

        return if (userIds.contains(localUserId)) resolveUsers(userIds)
        else resolveUsers(userIds.plus(localUserId))
    }

    fun getLocalUserId(): Long {
        var localUserId: Long? = null

        runBlocking {
            val accessTokenPayload = mTokenDataRepository.getAccessTokenPayload()
            val localUserIdClaim = accessTokenPayload[ACCESS_TOKEN_USER_ID_PAYLOAD_PROP_NAME]

            if (localUserIdClaim?.asLong() == null)
                throw ErrorAppException(mErrorDataRepository.getError(
                    DataTokenErrorType.INVALID_TOKEN_PAYLOAD.getErrorCode()))

            localUserId = localUserIdClaim.asLong()
        }

        return localUserId!!
    }

    private suspend fun resolveUserEntities(userEntities: List<UserEntity>): List<DataUser> {
        val avatarIds = userEntities.map { it.avatarId }.toSet().toList()
        val avatars = resolveAvatars(avatarIds)

        return userEntities.mapNotNull {
            val avatar = avatars[it.avatarId] ?: return@mapNotNull null

            it.toDataUser(avatar)
        }
    }

    private suspend fun resolveGetUserResponses(
        getUsersResponse: GetUsersResponse
    ): List<DataUser> {
        val avatarIds = getUsersResponse.users.map { it.avatarId }.toSet().toList()
        val avatars = resolveAvatars(avatarIds)

        return getUsersResponse.users.map { it.toDataUser(avatars[it.avatarId]!!) }
    }

    private suspend fun resolveAvatars(avatarIds: List<Long>): Map<Long, DataImage> {
        return mImageDataRepository.getImagesByIds(avatarIds).associateBy { it.id }
    }
}