package com.qubacy.geoqq.domain.common.usecase.util.extension.user

import android.net.Uri
import android.util.Log
import com.qubacy.geoqq.data.common.repository.common.result.common.Result
import com.qubacy.geoqq.data.common.repository.common.result.error.ErrorResult
import com.qubacy.geoqq.data.image.repository.ImageDataRepository
import com.qubacy.geoqq.data.image.repository.result.GetImagesResult
import com.qubacy.geoqq.data.token.repository.TokenDataRepository
import com.qubacy.geoqq.data.user.model.DataUser
import com.qubacy.geoqq.data.user.repository.UserDataRepository
import com.qubacy.geoqq.data.user.repository.result.GetUsersByIdsResult
import com.qubacy.geoqq.domain.common.model.User
import com.qubacy.geoqq.domain.common.operation.chat.SetUsersDetailsOperation
import com.qubacy.geoqq.domain.common.result.ProcessGetUserByIdResult
import com.qubacy.geoqq.domain.common.state.common.State
import com.qubacy.geoqq.domain.common.usecase.util.extension.image.ImageExtension
import com.qubacy.geoqq.domain.common.usecase.util.extension.token.TokenExtension
import com.qubacy.geoqq.domain.common.usecase.util.extension.token.result.GetAccessTokenResult
import com.qubacy.geoqq.domain.common.usecase.util.extension.user.result.GetDataUsersResult
import com.qubacy.geoqq.domain.common.usecase.util.extension.user.result.GetUsersAvatarUrisResult
import com.qubacy.geoqq.domain.common.usecase.util.extension.user.result.GetUsersFromGetUsersByIdsResult
import com.qubacy.geoqq.domain.common.usecase.util.extension.user.result.GetUsersResult
import com.qubacy.geoqq.domain.common.usecase.util.extension.user.result.ProcessDataUsersResult
import com.qubacy.geoqq.domain.mate.chats.MateChatsUseCase
import com.qubacy.geoqq.domain.mate.request.state.MateRequestsState

interface UserExtension {
    suspend fun getUsers(
        usersIds: List<Long>,
        tokenDataRepository: TokenDataRepository,
        tokenExtension: TokenExtension,
        userDataRepository: UserDataRepository,
        imageDataRepository: ImageDataRepository,
        imageExtension: ImageExtension,
        getUpdates: Boolean = true,
        preferLocal: Boolean = true
    ): Result {
        val getAccessTokenResult = tokenExtension.getAccessToken(tokenDataRepository)

        if (getAccessTokenResult is ErrorResult) return getAccessTokenResult

        val getAccessTokenResultCast = getAccessTokenResult as GetAccessTokenResult

        return getUsers(
            usersIds,
            getAccessTokenResultCast.accessToken,
            userDataRepository,
            imageDataRepository,
            imageExtension,
            getUpdates
        )
    }

    suspend fun getUsers(
        usersIds: List<Long>,
        accessToken: String,
        userDataRepository: UserDataRepository,
        imageDataRepository: ImageDataRepository,
        imageExtension: ImageExtension,
        getUpdates: Boolean = true,
        preferLocal: Boolean = true
    ): Result {
        val getDataUsersResult = getDataUsers(
            usersIds, accessToken, userDataRepository, getUpdates, preferLocal)

        if (getDataUsersResult is ErrorResult) return getDataUsersResult

        val getDataUsersResultCast = getDataUsersResult as GetDataUsersResult

        val getUsersAvatarUrisResult = getUsersAvatarUris(
            getDataUsersResultCast.dataUsers,
            accessToken,
            userDataRepository,
            imageDataRepository
        )

        if (getUsersAvatarUrisResult is ErrorResult) return getUsersAvatarUrisResult

        val users = (getUsersAvatarUrisResult as GetUsersAvatarUrisResult).avatarUrisMap.map { entry ->
            val dataUser = getDataUsersResultCast.dataUsers.find {it.id == entry.key}!!

            User(dataUser.id, dataUser.username, dataUser.description, entry.value, dataUser.isMate)
        }

        return GetUsersResult(users)
    }

    suspend fun processDataUsers(
        dataUsers: List<DataUser>,
        accessToken: String,
        userDataRepository: UserDataRepository,
        imageDataRepository: ImageDataRepository
    ): Result {
        val getUsersAvatarUrisResult = getUsersAvatarUris(
            dataUsers,
            accessToken,
            userDataRepository,
            imageDataRepository
        )

        if (getUsersAvatarUrisResult is ErrorResult) return getUsersAvatarUrisResult

        val getUsersAvatarUrisResultCast = getUsersAvatarUrisResult as GetUsersAvatarUrisResult

        val users = dataUsers.map {
            User(
                it.id,
                it.username,
                it.description,
                getUsersAvatarUrisResultCast.avatarUrisMap[it.id]!!,
                it.isMate
            )
        }

        return ProcessDataUsersResult(users)
    }

    private suspend fun getDataUsers(
        usersIds: List<Long>,
        accessToken: String,
        userDataRepository: UserDataRepository,
        getUpdates: Boolean,
        preferLocal: Boolean
    ): Result {
        val getUsersResult = userDataRepository
            .getUsersByIds(usersIds, accessToken, getUpdates, preferLocal)

        if (getUsersResult is ErrorResult) return getUsersResult

        val getUsersResultCast = getUsersResult as GetUsersByIdsResult

        return GetDataUsersResult(getUsersResultCast.users)
    }

    suspend fun getUsersAvatarUris(
        newUsersData: List<DataUser>,
        accessToken: String,
        userDataRepository: UserDataRepository,
        imageDataRepository: ImageDataRepository
    ): Result {
        val userIdToImageIdMap = newUsersData.map { Pair(it.id, it.avatarId) }
        val avatarsIds = newUsersData.map { it.avatarId }.distinct()

        val getImagesResult = imageDataRepository.getImages(avatarsIds, accessToken)

        if (getImagesResult is ErrorResult) return getImagesResult

        val getImagesResultCast = getImagesResult as GetImagesResult
        val userIdToAvatarUriMap = mutableMapOf<Long, Uri>()

        for (userIdImageIdPair in userIdToImageIdMap) {
            userIdToAvatarUriMap[userIdImageIdPair.first] =
                getImagesResultCast.imageIdToUriMap[userIdImageIdPair.second]!!
        }

        return GetUsersAvatarUrisResult(userIdToAvatarUriMap)
    }

    suspend fun getUsersFromGetUsersByIdsResult(
        getUsersByIdsResult: GetUsersByIdsResult,
        userDataRepository: UserDataRepository,
        imageDataRepository: ImageDataRepository,
        tokenDataRepository: TokenDataRepository,
        tokenExtension: TokenExtension
    ): Result {
        val getAccessTokenResult = tokenExtension.getAccessToken(tokenDataRepository)

        if (getAccessTokenResult is ErrorResult) return getAccessTokenResult

        val getAccessTokenResultCast = getAccessTokenResult as GetAccessTokenResult

        Log.d(MateChatsUseCase.TAG, "getUsersFromGetUsersByIdsResult(): users.size = ${getUsersByIdsResult.users.size}; areLocal = ${getUsersByIdsResult.areLocal}")

        val processDataUsersResult = processDataUsers(
            getUsersByIdsResult.users, getAccessTokenResultCast.accessToken,
            userDataRepository, imageDataRepository
        )

        if (processDataUsersResult is ErrorResult) return processDataUsersResult

        return GetUsersFromGetUsersByIdsResult(
            (processDataUsersResult as ProcessDataUsersResult).users)
    }
}