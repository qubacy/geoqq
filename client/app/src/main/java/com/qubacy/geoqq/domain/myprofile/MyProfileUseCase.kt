package com.qubacy.geoqq.domain.myprofile

import android.graphics.Bitmap
import android.net.Uri
import com.qubacy.geoqq.data.common.operation.Operation
import com.qubacy.geoqq.data.common.repository.common.result.error.ErrorResult
import com.qubacy.geoqq.data.common.repository.common.result.interruption.InterruptionResult
import com.qubacy.geoqq.data.error.repository.ErrorDataRepository
import com.qubacy.geoqq.data.image.repository.ImageDataRepository
import com.qubacy.geoqq.data.image.repository.result.GetImageByUriResult
import com.qubacy.geoqq.data.myprofile.model.common.MyProfileDataModelContext
import com.qubacy.geoqq.data.myprofile.repository.MyProfileDataRepository
import com.qubacy.geoqq.data.token.repository.TokenDataRepository
import com.qubacy.geoqq.data.token.repository.result.GetTokensResult
import com.qubacy.geoqq.domain.common.UseCase
import com.qubacy.geoqq.domain.myprofile.state.MyProfileState

class MyProfileUseCase(
    errorDataRepository: ErrorDataRepository,
    val tokenDataRepository: TokenDataRepository,
    val myProfileDataRepository: MyProfileDataRepository,
    val imageDataRepository: ImageDataRepository
) : UseCase<MyProfileState>(errorDataRepository) {
    override fun generateState(operations: List<Operation>): MyProfileState {
        val prevState = stateFlow.value
        val state = if (prevState == null) {
            MyProfileState(newOperations = operations)
        }
        else {
            MyProfileState(
                prevState.avatar,
                prevState.username,
                prevState.description,
                prevState.hitUpOption,
                operations
            )
        }

        return state
    }

    suspend fun getMyProfile() {
        mCurrentRepository = tokenDataRepository
        val getTokensResult = tokenDataRepository.getTokens()

        if (getTokensResult is ErrorResult) return processError(getTokensResult.errorId)
        if (getTokensResult is InterruptionResult) return processInterruption()

        val accessToken = (getTokensResult as GetTokensResult).accessToken

        mCurrentRepository = myProfileDataRepository
        myProfileDataRepository.getMyProfile(accessToken)
    }

    suspend fun updateMyProfile(
        avatarUri: Uri?,
        description: String?,
        password: String?,
        newPassword: String?,
        hitUpOption: MyProfileDataModelContext.HitUpOption?
    ) {
        var avatarBitmap: Bitmap? = null

        if (avatarUri != null) {
            mCurrentRepository = imageDataRepository
            val getImageResult = imageDataRepository.getImageByUri(avatarUri)

            if (getImageResult is ErrorResult) return processError(getImageResult.errorId)
            if (getImageResult is InterruptionResult) return processInterruption()

            avatarBitmap = (getImageResult as GetImageByUriResult).imageBitmap
        }

        mCurrentRepository = tokenDataRepository
        val getTokensResult = tokenDataRepository.getTokens()

        if (getTokensResult is ErrorResult) return processError(getTokensResult.errorId)
        if (getTokensResult is InterruptionResult) return processInterruption()

        val accessToken = (getTokensResult as GetTokensResult).accessToken

        mCurrentRepository = myProfileDataRepository
        myProfileDataRepository.updateMyProfile(
            accessToken,
            avatarBitmap,
            description,
            password,
            newPassword,
            hitUpOption
        )
    }
}