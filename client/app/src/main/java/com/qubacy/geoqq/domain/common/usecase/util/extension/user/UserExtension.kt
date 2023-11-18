package com.qubacy.geoqq.domain.common.usecase.util.extension.user

import android.net.Uri
import com.qubacy.geoqq.data.common.repository.common.result.common.Result
import com.qubacy.geoqq.data.common.repository.common.result.error.ErrorResult
import com.qubacy.geoqq.data.image.repository.ImageDataRepository
import com.qubacy.geoqq.data.image.repository.result.GetImagesResult
import com.qubacy.geoqq.data.user.model.DataUser
import com.qubacy.geoqq.data.user.repository.UserDataRepository
import com.qubacy.geoqq.data.user.repository.result.GetUsersByIdsResult
import com.qubacy.geoqq.domain.common.model.User
import com.qubacy.geoqq.domain.common.usecase.util.extension.image.ImageExtension
import com.qubacy.geoqq.domain.common.usecase.util.extension.user.result.GetDataUsersResult
import com.qubacy.geoqq.domain.common.usecase.util.extension.user.result.GetUsersAvatarUrisResult
import com.qubacy.geoqq.domain.common.usecase.util.extension.user.result.GetUsersResult

interface UserExtension {
    suspend fun getUsers(
        usersIds: List<Long>,
        accessToken: String,
        userDataRepository: UserDataRepository,
        imageDataRepository: ImageDataRepository,
        imageExtension: ImageExtension
    ): Result {
        val getDataUsersResult = getDataUsers(usersIds, accessToken, userDataRepository)

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

    private suspend fun getDataUsers(
        usersIds: List<Long>, accessToken: String, userDataRepository: UserDataRepository
    ): Result {
        val getUsersResult = userDataRepository.getUsersByIds(usersIds, accessToken)

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
}