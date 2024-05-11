package com.qubacy.geoqq.data.user.repository.impl

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.qubacy.geoqq._common.exception.error.ErrorAppException
import com.qubacy.geoqq._common.util.livedata.extension.awaitUntilVersion
import com.qubacy.geoqq.data.image.model.DataImage
import com.qubacy.geoqq.data._common.repository._common.error.type.token.DataTokenErrorType
import com.qubacy.geoqq.data._common.repository._common.source.local.database.error._common.LocalErrorDatabaseDataSource
import com.qubacy.geoqq.data._common.repository._common.source.local.datastore.token._common.LocalTokenDataStoreDataSource
import com.qubacy.geoqq.data._common.repository._common.util.token.TokenUtils
import com.qubacy.geoqq.data.image.repository._common.ImageDataRepository
import com.qubacy.geoqq.data.user.model.DataUser
import com.qubacy.geoqq.data.user.model.toDataUser
import com.qubacy.geoqq.data.user.model.toUserEntity
import com.qubacy.geoqq.data.user.repository._common.UserDataRepository
import com.qubacy.geoqq.data.user.repository._common.result.GetUsersByIdsDataResult
import com.qubacy.geoqq.data.user.repository._common.result.ResolveUsersDataResult
import com.qubacy.geoqq.data.user.repository._common.source.local.database._common.LocalUserDatabaseDataSource
import com.qubacy.geoqq.data.user.repository._common.source.remote.http.rest._common.api.response.GetUsersResponse
import com.qubacy.geoqq.data.user.repository._common.source.local.database._common.entity.UserEntity
import com.qubacy.geoqq.data.user.repository._common.source.remote.http.rest._common.RemoteUserHttpRestDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import kotlin.coroutines.coroutineContext

open class UserDataRepositoryImpl @Inject constructor(
    coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO,
    coroutineScope: CoroutineScope = CoroutineScope(coroutineDispatcher),
    private val mErrorSource: LocalErrorDatabaseDataSource,
    private val mImageDataRepository: ImageDataRepository,
    private val mLocalTokenDataStoreDataSource: LocalTokenDataStoreDataSource,
    private val mLocalUserDatabaseDataSource: LocalUserDatabaseDataSource,
    private val mRemoteUserHttpRestDataSource: RemoteUserHttpRestDataSource
    // todo: add a websocket source..
) : UserDataRepository(coroutineDispatcher, coroutineScope) {
    companion object {
        const val ACCESS_TOKEN_USER_ID_PAYLOAD_PROP_NAME = "user-id"
    }

    override suspend fun getUsersByIds(userIds: List<Long>): LiveData<GetUsersByIdsDataResult> {
        val resultLiveData = MutableLiveData<GetUsersByIdsDataResult>()

        CoroutineScope(coroutineContext).launch {
            val localUsers = mLocalUserDatabaseDataSource.getUsersByIds(userIds)
            val localDataUsers = localUsers?.let { resolveUserEntities(it) }

            if (localUsers != null)
                resultLiveData.postValue(GetUsersByIdsDataResult(false, localDataUsers!!))

            val getUsersResponse = mRemoteUserHttpRestDataSource.getUsers(userIds)

            val httpDataUsers = resolveGetUserResponses(getUsersResponse)

            if (localDataUsers?.containsAll(httpDataUsers) == true) return@launch

            resultLiveData.postValue(GetUsersByIdsDataResult(true, httpDataUsers))

            val usersToSave = httpDataUsers.map { it.toUserEntity() }

            mLocalUserDatabaseDataSource.saveUsers(usersToSave)
        }

        return resultLiveData
    }

    override suspend fun resolveUsers(userIds: List<Long>): LiveData<ResolveUsersDataResult> {
        val resultLiveData = MutableLiveData<ResolveUsersDataResult>()

        CoroutineScope(coroutineContext).launch {
            val getUsersByIdsLiveData = getUsersByIds(userIds)

            var version = 0

            while (true) {
                val getUsersByIdsResult = getUsersByIdsLiveData.awaitUntilVersion(version)
                val userIdUserMap = getUsersByIdsResult.users.associateBy { it.id }

                ++version

                resultLiveData.postValue(
                    ResolveUsersDataResult(
                    getUsersByIdsResult.isNewest, userIdUserMap)
                )

                if (getUsersByIdsResult.isNewest) return@launch
            }
        }

        return resultLiveData
    }

    override fun getLocalUserId(): Long {
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