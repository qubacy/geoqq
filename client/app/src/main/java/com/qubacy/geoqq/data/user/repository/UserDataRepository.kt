package com.qubacy.geoqq.data.user.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.qubacy.geoqq._common.exception.error.ErrorAppException
import com.qubacy.geoqq._common.util.livedata.extension.await
import com.qubacy.geoqq._common.util.livedata.extension.awaitUntilVersion
import com.qubacy.geoqq.data._common.repository.producing.ProducingDataRepository
import com.qubacy.geoqq.data.image.model.DataImage
import com.qubacy.geoqq.data.image.repository.ImageDataRepository
import com.qubacy.geoqq.data._common.repository._common.error.type.token.DataTokenErrorType
import com.qubacy.geoqq.data._common.repository._common.source.local.database.error.LocalErrorDataSource
import com.qubacy.geoqq.data._common.repository._common.source.local.datastore.token.LocalTokenDataStoreDataSource
import com.qubacy.geoqq.data._common.repository._common.util.token.TokenUtils
import com.qubacy.geoqq.data.user.model.DataUser
import com.qubacy.geoqq.data.user.model.toDataUser
import com.qubacy.geoqq.data.user.model.toUserEntity
import com.qubacy.geoqq.data.user.repository.result.GetUsersByIdsDataResult
import com.qubacy.geoqq.data.user.repository.result.ResolveUsersDataResult
import com.qubacy.geoqq.data.user.repository.source.http.HttpUserDataSource
import com.qubacy.geoqq.data.user.repository.source.http.api.response.GetUsersResponse
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
    private val mErrorSource: LocalErrorDataSource,
    private val mImageDataRepository: ImageDataRepository,
    private val mLocalTokenDataStoreDataSource: LocalTokenDataStoreDataSource,
    private val mLocalUserDataSource: LocalUserDataSource,
    private val mHttpUserDataSource: HttpUserDataSource
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
                resultLiveData.postValue(GetUsersByIdsDataResult(false, localDataUsers!!))

            val getUsersResponse = mHttpUserDataSource.getUsers(userIds)

            val httpDataUsers = resolveGetUserResponses(getUsersResponse)

            if (localDataUsers?.containsAll(httpDataUsers) == true) return@launch

            resultLiveData.postValue(GetUsersByIdsDataResult(true, httpDataUsers))

            val usersToSave = httpDataUsers.map { it.toUserEntity() }

            mLocalUserDataSource.saveUsers(usersToSave)
        }

        return resultLiveData
    }

    suspend fun resolveUsers(userIds: List<Long>): LiveData<ResolveUsersDataResult> {
        val resultLiveData = MutableLiveData<ResolveUsersDataResult>()

        CoroutineScope(coroutineContext).launch {
            val getUsersByIdsLiveData = getUsersByIds(userIds)

            var version = 0

            while (true) {
                val getUsersByIdsResult = getUsersByIdsLiveData.awaitUntilVersion(version)
                val userIdUserMap = getUsersByIdsResult.users.associateBy { it.id }

                ++version

                resultLiveData.postValue(ResolveUsersDataResult(
                    getUsersByIdsResult.isNewest, userIdUserMap))

                if (getUsersByIdsResult.isNewest) return@launch
            }
        }

        return resultLiveData
    }

    suspend fun resolveLocalUser(): DataUser {
        val localUserId = getLocalUserId()

        return getUsersByIds(listOf(localUserId)).await().users.first()
    }

    suspend fun resolveUsersWithLocalUser(
        userIds: List<Long>
    ): LiveData<ResolveUsersDataResult> {
        val localUserId = getLocalUserId()

        return if (userIds.contains(localUserId)) resolveUsers(userIds)
        else resolveUsers(userIds.plus(localUserId))
    }

    fun getLocalUserId(): Long {
        var localUserId: Long? = null

        runBlocking {
            val localAccessToken = mLocalTokenDataStoreDataSource.getAccessToken()!! // todo: is it ok?
            val accessTokenPayload = TokenUtils.getTokenPayload(localAccessToken, mErrorSource)
            val localUserIdClaim = accessTokenPayload[ACCESS_TOKEN_USER_ID_PAYLOAD_PROP_NAME]

            if (localUserIdClaim?.asLong() == null)
                throw ErrorAppException(mErrorSource.getError(
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