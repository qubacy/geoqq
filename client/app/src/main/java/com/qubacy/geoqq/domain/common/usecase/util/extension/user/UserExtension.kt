package com.qubacy.geoqq.domain.common.usecase.util.extension.user

import android.net.Uri
import com.qubacy.geoqq.data.common.repository.common.result.common.Result
import com.qubacy.geoqq.data.common.repository.common.result.error.ErrorResult
import com.qubacy.geoqq.data.image.repository.ImageDataRepository
import com.qubacy.geoqq.data.image.repository.result.GetImagesIdsByUrisResult
import com.qubacy.geoqq.data.image.repository.result.GetImageResult
import com.qubacy.geoqq.data.user.model.DataUser
import com.qubacy.geoqq.data.user.repository.UserDataRepository
import com.qubacy.geoqq.data.user.repository.result.GetUsersByIdsResult
import com.qubacy.geoqq.domain.common.model.User
import com.qubacy.geoqq.domain.common.usecase.util.extension.image.ImageExtension
import com.qubacy.geoqq.domain.common.usecase.util.extension.image.result.GetImageUriResult
import com.qubacy.geoqq.domain.common.usecase.util.extension.user.result.GetAvatarUriResult
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



        val getUsersAvatarUrisResult = getUsersAvatarUrisWithPrevDataUsers(
            getDataUsersResultCast.dataUsers,
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

    suspend fun getUsersAvatarUrisWithPrevUsers(
        newUsersData: List<DataUser>,
        prevUsers: List<User>?,
        accessToken: String,
        userDataRepository: UserDataRepository,
        imageDataRepository: ImageDataRepository
    ): Result {
        return getUserAvatarUri(
            newUsersData, null,
            prevUsers, accessToken,
            userDataRepository, imageDataRepository
        )
    }

    suspend fun getUsersAvatarUrisWithPrevDataUsers(
        newUsersData: List<DataUser>,
        prevDataUsers: List<DataUser>?,
        accessToken: String,
        userDataRepository: UserDataRepository,
        imageDataRepository: ImageDataRepository
    ): Result {
        return getUserAvatarUri(
            newUsersData, prevDataUsers,
            null, accessToken,
            userDataRepository, imageDataRepository
        )
    }

    private suspend fun getUserAvatarUri(
        newUsersData: List<DataUser>,
        prevDataUsers: List<DataUser>?,
        prevUsers: List<User>?,
        accessToken: String,
        userDataRepository: UserDataRepository,
        imageDataRepository: ImageDataRepository
    ): Result {
        val loadedPrevDataUsers = if (prevUsers == null) {
            if (prevDataUsers == null) {
                val getUsersByIdResult = userDataRepository
                    .getUsersByIds(newUsersData.map { it.id }, accessToken, false)

                if (getUsersByIdResult is ErrorResult) return getUsersByIdResult

                (getUsersByIdResult as GetUsersByIdsResult).users

            } else {
                prevDataUsers
            }

        } else {
            null
        }

        val prevUsersAvatarIds = if (prevUsers == null) {
            loadedPrevDataUsers!!.map { it.avatarId }
        } else {
            val getPrevUsersAvatarIdsResult = imageDataRepository
                .getImagesIdsByUris(prevUsers.map { it.avatarUri })

            if (getPrevUsersAvatarIdsResult is ErrorResult) return getPrevUsersAvatarIdsResult

            (getPrevUsersAvatarIdsResult as GetImagesIdsByUrisResult).imagesIds
        }

        val avatarUrisMap = mutableMapOf<Long, Uri>()

        for (i in prevUsersAvatarIds.indices) {
            val getAvatarUriResult = getAvatarUri(
                prevUsers?.get(i),
                newUsersData[i],
                loadedPrevDataUsers?.get(i),
                prevUsersAvatarIds[i],
                accessToken,
                imageDataRepository
            )

            if (getAvatarUriResult is ErrorResult) return getAvatarUriResult

            avatarUrisMap.put(
                newUsersData[i].id,
                (getAvatarUriResult as GetAvatarUriResult).avatarUri
            )
        }

        return GetUsersAvatarUrisResult(avatarUrisMap)
    }

    private suspend fun getAvatarUri(
        prevUser: User?,
        newUserData: DataUser,
        loadedPrevDataUser: DataUser?,
        prevUserAvatarId: Long,
        accessToken: String,
        imageDataRepository: ImageDataRepository
    ): Result {
        val avatarUri = if (prevUserAvatarId == newUserData.avatarId) {
            if (prevUser == null) {
                val getImageResult = imageDataRepository.getImage(
                    loadedPrevDataUser!!.avatarId, accessToken, false)

                if (getImageResult is ErrorResult) return getImageResult

                (getImageResult as GetImageResult).imageUri

            } else {
                prevUser.avatarUri
            }

        } else {
            val getImageUriResult = imageDataRepository.getImage(
                newUserData.avatarId,
                accessToken
            )

            if (getImageUriResult is ErrorResult) return getImageUriResult

            (getImageUriResult as GetImageUriResult).imageUri
        }

        return GetAvatarUriResult(avatarUri)
    }
}