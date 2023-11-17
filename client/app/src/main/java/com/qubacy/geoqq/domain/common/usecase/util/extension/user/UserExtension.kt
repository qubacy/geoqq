package com.qubacy.geoqq.domain.common.usecase.util.extension.user

import com.qubacy.geoqq.data.common.repository.common.result.common.Result
import com.qubacy.geoqq.data.common.repository.common.result.error.ErrorResult
import com.qubacy.geoqq.data.image.repository.ImageDataRepository
import com.qubacy.geoqq.data.image.repository.result.GetImageIdByUriResult
import com.qubacy.geoqq.data.image.repository.result.GetImageResult
import com.qubacy.geoqq.data.user.model.DataUser
import com.qubacy.geoqq.data.user.repository.UserDataRepository
import com.qubacy.geoqq.data.user.repository.result.GetUserByIdResult
import com.qubacy.geoqq.domain.common.model.User
import com.qubacy.geoqq.domain.common.usecase.util.extension.image.ImageExtension
import com.qubacy.geoqq.domain.common.usecase.util.extension.image.result.GetImageUriResult
import com.qubacy.geoqq.domain.common.usecase.util.extension.user.result.GetDataUserResult
import com.qubacy.geoqq.domain.common.usecase.util.extension.user.result.GetUserAvatarUriResult
import com.qubacy.geoqq.domain.common.usecase.util.extension.user.result.GetUserResult

interface UserExtension {
    suspend fun getUser(
        userId: Long,
        accessToken: String,
        userDataRepository: UserDataRepository,
        imageDataRepository: ImageDataRepository,
        imageExtension: ImageExtension
    ): Result {
        val getDataUserResult = getDataUser(userId, accessToken, userDataRepository)

        if (getDataUserResult is ErrorResult) return getDataUserResult

        val getDataUserResultCast = getDataUserResult as GetDataUserResult

        val getUserAvatarUriResult = getUserAvatarUriWithPrevDataUser(
            getDataUserResultCast.dataUser,
            getDataUserResultCast.dataUser,
            accessToken,
            userDataRepository,
            imageDataRepository
        )

        if (getUserAvatarUriResult is ErrorResult) return getUserAvatarUriResult

        val user = User(
            getDataUserResultCast.dataUser.id,
            getDataUserResultCast.dataUser.username,
            getDataUserResultCast.dataUser.description,
            (getUserAvatarUriResult as GetUserAvatarUriResult).avatarUri,
            getDataUserResultCast.dataUser.isMate
        )

        return GetUserResult(user)
    }

    suspend fun getDataUser(
        userId: Long, accessToken: String, userDataRepository: UserDataRepository
    ): Result {
        val getUserResult = userDataRepository.getUserById(userId, accessToken)

        if (getUserResult is ErrorResult) return getUserResult

        val getUserResultCast = getUserResult as GetUserByIdResult

        return GetDataUserResult(getUserResultCast.user)
    }

//    suspend fun getUserAvatarUri(
//        newUserData: DataUser,
//        prevUser: User?,
//        accessToken: String,
//        userDataRepository: UserDataRepository,
//        imageDataRepository: ImageDataRepository
//    ): Result {
//        var loadedPrevUserData: DataUser? = null
//
//        if (prevUser == null) {
//            val getUserByIdResult =
//                userDataRepository.getUserById(newUserData.id, accessToken, false)
//
//            if (getUserByIdResult is ErrorResult) return getUserByIdResult
//
//            loadedPrevUserData = (getUserByIdResult as GetUserByIdResult).user
//        }
//
//        val prevUserAvatarId = if (prevUser == null) {
//            loadedPrevUserData!!.avatarId
//        } else {
//            val getPrevUserAvatarIdResult = imageDataRepository.getImageIdByUri(prevUser.avatarUri)
//
//            if (getPrevUserAvatarIdResult is ErrorResult) return getPrevUserAvatarIdResult
//
//            (getPrevUserAvatarIdResult as GetImageIdByUriResult).imageId
//        }
//
//        val avatarUri = if (prevUserAvatarId == newUserData.avatarId) {
//            if (prevUser == null) {
//                val getImageResult = imageDataRepository.getImage(
//                    loadedPrevUserData!!.avatarId, accessToken, false)
//
//                if (getImageResult is ErrorResult) return getImageResult
//
//                (getImageResult as GetImageResult).imageUri
//
//            } else {
//                prevUser.avatarUri
//            }
//
//        } else {
//            val getImageUriResult = imageDataRepository.getImage(
//                newUserData.avatarId,
//                accessToken
//            )
//
//            if (getImageUriResult is ErrorResult) return getImageUriResult
//
//            (getImageUriResult as GetImageUriResult).imageUri
//        }
//
//        return GetUserAvatarUriResult(avatarUri)
//    }

    suspend fun getUserAvatarUriWithPrevUser(
        newUserData: DataUser,
        prevUser: User?,
        accessToken: String,
        userDataRepository: UserDataRepository,
        imageDataRepository: ImageDataRepository
    ): Result {
        return getUserAvatarUri(
            newUserData, null,
            prevUser, accessToken,
            userDataRepository, imageDataRepository
        )
    }

    suspend fun getUserAvatarUriWithPrevDataUser(
        newUserData: DataUser,
        prevDataUser: DataUser?,
        accessToken: String,
        userDataRepository: UserDataRepository,
        imageDataRepository: ImageDataRepository
    ): Result {
        return getUserAvatarUri(
            newUserData, prevDataUser,
            null, accessToken,
            userDataRepository, imageDataRepository
        )
    }

    private suspend fun getUserAvatarUri(
        newUserData: DataUser,
        prevDataUser: DataUser?,
        prevUser: User?,
        accessToken: String,
        userDataRepository: UserDataRepository,
        imageDataRepository: ImageDataRepository
    ): Result {
        val loadedPrevDataUser = if (prevUser == null) {
            if (prevDataUser == null) {
                val getUserByIdResult =
                    userDataRepository.getUserById(newUserData.id, accessToken, false)

                if (getUserByIdResult is ErrorResult) return getUserByIdResult

                (getUserByIdResult as GetUserByIdResult).user

            } else {
                prevDataUser
            }

        } else {
            null
        }

        val prevUserAvatarId = if (prevUser == null) {
            loadedPrevDataUser!!.avatarId
        } else {
            val getPrevUserAvatarIdResult = imageDataRepository.getImageIdByUri(prevUser.avatarUri)

            if (getPrevUserAvatarIdResult is ErrorResult) return getPrevUserAvatarIdResult

            (getPrevUserAvatarIdResult as GetImageIdByUriResult).imageId
        }

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

        return GetUserAvatarUriResult(avatarUri)
    }
}