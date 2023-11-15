package com.qubacy.geoqq.domain.common.usecase.util.extension.user

import com.qubacy.geoqq.data.common.repository.common.result.common.Result
import com.qubacy.geoqq.data.common.repository.common.result.error.ErrorResult
import com.qubacy.geoqq.data.image.repository.ImageDataRepository
import com.qubacy.geoqq.data.user.repository.UserDataRepository
import com.qubacy.geoqq.data.user.repository.result.GetUserByIdResult
import com.qubacy.geoqq.domain.common.model.User
import com.qubacy.geoqq.domain.common.usecase.util.extension.image.ImageExtension
import com.qubacy.geoqq.domain.common.usecase.util.extension.image.result.GetImageUriResult
import com.qubacy.geoqq.domain.common.usecase.util.extension.user.result.GetDataUserResult
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

        val getImageUriResult = imageExtension.getImageUri(
            getDataUserResultCast.dataUser.avatarId, accessToken, imageDataRepository
        )

        if (getImageUriResult is ErrorResult) return getImageUriResult

        val getImageUriResultCast = getImageUriResult as GetImageUriResult

        val user = User(
            getDataUserResultCast.dataUser.id,
            getDataUserResultCast.dataUser.username,
            getDataUserResultCast.dataUser.description,
            getImageUriResultCast.imageUri,
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
}