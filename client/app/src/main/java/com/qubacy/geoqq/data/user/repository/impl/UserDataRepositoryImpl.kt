package com.qubacy.geoqq.data.user.repository.impl

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.qubacy.geoqq._common.exception.error.ErrorAppException
import com.qubacy.geoqq._common.util.livedata.extension.awaitUntilVersion
import com.qubacy.geoqq.data.image.model.DataImage
import com.qubacy.geoqq.data._common.repository.token.error.type.DataTokenErrorType
import com.qubacy.geoqq.data._common.repository._common.result.DataResult
import com.qubacy.geoqq.data._common.repository._common.source.local.database.error._common.LocalErrorDatabaseDataSource
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.result.payload.WebSocketPayloadResult
import com.qubacy.geoqq.data._common.repository.token.repository._common.source.local.datastore._common.LocalTokenDataStoreDataSource
import com.qubacy.geoqq.data._common.repository.aspect.websocket.WebSocketEventDataRepository
import com.qubacy.geoqq.data._common.repository.producing.source.ProducingDataSource
import com.qubacy.geoqq.data._common.repository.token.repository._common.util.TokenUtils
import com.qubacy.geoqq.data.image.repository._common.ImageDataRepository
import com.qubacy.geoqq.data.user.model.DataUser
import com.qubacy.geoqq.data.user.model.toDataUser
import com.qubacy.geoqq.data.user.model.toUserEntity
import com.qubacy.geoqq.data.user.repository._common.UserDataRepository
import com.qubacy.geoqq.data.user.repository._common.result.get.GetUsersByIdsDataResult
import com.qubacy.geoqq.data.user.repository._common.result.resolve.ResolveUsersDataResult
import com.qubacy.geoqq.data.user.repository._common.result.updated.UserUpdatedDataResult
import com.qubacy.geoqq.data.user.repository._common.source.local.database._common.LocalUserDatabaseDataSource
import com.qubacy.geoqq.data.user.repository._common.source.remote.http.rest._common.api.response.GetUsersResponse
import com.qubacy.geoqq.data.user.repository._common.source.local.database._common.entity.UserEntity
import com.qubacy.geoqq.data.user.repository._common.source.remote.http.rest._common.RemoteUserHttpRestDataSource
import com.qubacy.geoqq.data.user.repository._common.source.remote.http.websocket._common.RemoteUserHttpWebSocketDataSource
import com.qubacy.geoqq.data.user.repository._common.source.remote.http.websocket._common.event.server.payload.updated.UserUpdatedServerEventPayload
import com.qubacy.geoqq.data.user.repository._common.source.remote.http.websocket._common.event.server.type.UserServerEventType
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.coroutines.coroutineContext

open class UserDataRepositoryImpl(
    coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO,
    coroutineScope: CoroutineScope = CoroutineScope(coroutineDispatcher),
    private val mErrorSource: LocalErrorDatabaseDataSource,
    private val mImageDataRepository: ImageDataRepository,
    private val mLocalTokenDataStoreDataSource: LocalTokenDataStoreDataSource,
    private val mLocalUserDatabaseDataSource: LocalUserDatabaseDataSource,
    private val mRemoteUserHttpRestDataSource: RemoteUserHttpRestDataSource,
    private val mRemoteUserHttpWebSocketDataSource: RemoteUserHttpWebSocketDataSource
) : UserDataRepository(coroutineDispatcher, coroutineScope), WebSocketEventDataRepository {
    companion object {
        const val ACCESS_TOKEN_USER_ID_PAYLOAD_PROP_NAME = "user-id"
    }

    override val resultFlow: Flow<DataResult> = merge(
        mResultFlow,
        mRemoteUserHttpWebSocketDataSource.eventFlow
            .mapNotNull { mapWebSocketResultToDataResult(it) }
    )

    override fun getProducingDataSources(): Array<ProducingDataSource> {
        return arrayOf(mRemoteUserHttpWebSocketDataSource)
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
            mRemoteUserHttpWebSocketDataSource.startProducing() // todo: ok?
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

                if (getUsersByIdsResult.isNewest)
                    return@launch mRemoteUserHttpWebSocketDataSource.startProducing() // todo: ok?
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

    override fun processWebSocketPayloadResult(
        webSocketPayloadResult: WebSocketPayloadResult
    ): DataResult? {
        return when (webSocketPayloadResult.type) {
            UserServerEventType.USER_UPDATED_EVENT_TYPE_NAME.title ->
                processUserUpdatedServerEventPayload(
                    webSocketPayloadResult.payload as UserUpdatedServerEventPayload)
            else -> throw IllegalArgumentException()
        }
    }

    private fun processUserUpdatedServerEventPayload(
        payload: UserUpdatedServerEventPayload
    ): DataResult {
        lateinit var dataUser: DataUser

        // todo: alright?:
        runBlocking {
            val avatar = mImageDataRepository.getImageById(payload.avatarId)

            dataUser = payload.toDataUser(avatar)
        }

        mLocalUserDatabaseDataSource.updateUser(dataUser.toUserEntity())

        return UserUpdatedDataResult(dataUser)
    }
}