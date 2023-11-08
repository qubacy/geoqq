package com.qubacy.geoqq.domain.myprofile

import android.graphics.Bitmap
import android.net.Uri
import com.qubacy.geoqq.data.common.operation.Operation
import com.qubacy.geoqq.data.common.repository.common.result.common.Result
import com.qubacy.geoqq.data.common.repository.common.result.error.ErrorResult
import com.qubacy.geoqq.data.common.repository.common.result.interruption.InterruptionResult
import com.qubacy.geoqq.data.error.repository.ErrorDataRepository
import com.qubacy.geoqq.data.image.repository.ImageDataRepository
import com.qubacy.geoqq.data.image.repository.result.GetImageByUriResult
import com.qubacy.geoqq.data.image.repository.result.GetImageResult
import com.qubacy.geoqq.data.myprofile.model.avatar.labeled.DataMyProfileWithAvatarId
import com.qubacy.geoqq.data.myprofile.model.avatar.linked.DataMyProfileWithLinkedAvatar
import com.qubacy.geoqq.data.myprofile.model.common.MyProfileDataModelContext
import com.qubacy.geoqq.data.myprofile.repository.MyProfileDataRepository
import com.qubacy.geoqq.data.myprofile.repository.result.GetMyProfileResult
import com.qubacy.geoqq.data.myprofile.repository.result.UpdateMyProfileResult
import com.qubacy.geoqq.data.token.repository.TokenDataRepository
import com.qubacy.geoqq.data.token.repository.result.GetTokensResult
import com.qubacy.geoqq.domain.common.UseCase
import com.qubacy.geoqq.domain.myprofile.operation.SuccessfulProfileSavingCallbackOperation
import com.qubacy.geoqq.domain.myprofile.state.MyProfileState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MyProfileUseCase(
    errorDataRepository: ErrorDataRepository,
    val tokenDataRepository: TokenDataRepository,
    val myProfileDataRepository: MyProfileDataRepository,
    val imageDataRepository: ImageDataRepository
) : UseCase<MyProfileState>(errorDataRepository) {
    init {
        mCoroutineScope.launch(Dispatchers.IO) {
            myProfileDataRepository.resultFlow.collect {
                processResult(it)
            }
        }
    }

    private suspend fun processResult(result: Result) {
        when (result::class) {
            GetMyProfileResult::class -> {
                val getMyProfileResult = result as GetMyProfileResult

                processGetMyProfileResult(getMyProfileResult)
            }
            UpdateMyProfileResult::class -> {
                val updateMyProfileResult = result as UpdateMyProfileResult

                processUpdateMyProfileResult(updateMyProfileResult)
            }
            InterruptionResult::class -> {
                val interruptionResult = result as InterruptionResult

                processInterruption()
            }
            ErrorResult::class -> {
                val errorResult = result as ErrorResult

                processError(errorResult.errorId)
            }
        }
    }

    private suspend fun processGetMyProfileResult(getMyProfileResult: GetMyProfileResult) {
        val operations = listOf<Operation>()

        val state = if (getMyProfileResult.myProfileData is DataMyProfileWithAvatarId) {
            mCurrentRepository = tokenDataRepository
            val getTokensResult = tokenDataRepository.getTokens()

            if (getTokensResult is ErrorResult) return processError(getTokensResult.errorId)
            if (getTokensResult is InterruptionResult) return processInterruption()

            val accessToken = (getTokensResult as GetTokensResult).accessToken

            mCurrentRepository = imageDataRepository
            val getImageResult = imageDataRepository.getImage(
                getMyProfileResult.myProfileData.avatarId, accessToken)

            if (getImageResult is ErrorResult) return processError(getImageResult.errorId)
            if (getImageResult is InterruptionResult) return processInterruption()

            val getImageResultCast = getImageResult as GetImageResult

            MyProfileState(
                getImageResultCast.imageUri,
                getMyProfileResult.myProfileData.username,
                getMyProfileResult.myProfileData.description,
                getMyProfileResult.myProfileData.hitUpOption,
                operations
            )

        } else if (getMyProfileResult.myProfileData is DataMyProfileWithLinkedAvatar) {
            MyProfileState(
                getMyProfileResult.myProfileData.avatarUri,
                getMyProfileResult.myProfileData.username,
                getMyProfileResult.myProfileData.description,
                getMyProfileResult.myProfileData.hitUpOption,
                operations
            )

        } else
            throw IllegalStateException()

        mStateFlow.emit(state)
    }

    private suspend fun processUpdateMyProfileResult(
        updateMyProfileResult: UpdateMyProfileResult
    ) {
        val operations = listOf(
            SuccessfulProfileSavingCallbackOperation()
        )
        val state = generateState(operations)

        mStateFlow.emit(state)
    }

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

    fun getMyProfile() {
        mCoroutineScope.launch(Dispatchers.IO) {
            mCurrentRepository = tokenDataRepository
            val getTokensResult = tokenDataRepository.getTokens()

            if (getTokensResult is ErrorResult) return@launch processError(getTokensResult.errorId)
            if (getTokensResult is InterruptionResult) return@launch processInterruption()

            val accessToken = (getTokensResult as GetTokensResult).accessToken

            mCurrentRepository = myProfileDataRepository
            myProfileDataRepository.getMyProfile(accessToken)
        }
    }

    fun updateMyProfile(
        avatarUri: Uri?,
        description: String?,
        password: String?,
        newPassword: String?,
        hitUpOption: MyProfileDataModelContext.HitUpOption?
    ) {
        mCoroutineScope.launch {
            var avatarBitmap: Bitmap? = null

            if (avatarUri != null) {
                mCurrentRepository = imageDataRepository
                val getImageResult = imageDataRepository.getImageByUri(avatarUri)

                if (getImageResult is ErrorResult) return@launch processError(getImageResult.errorId)
                if (getImageResult is InterruptionResult) return@launch processInterruption()

                avatarBitmap = (getImageResult as GetImageByUriResult).imageBitmap
            }

            mCurrentRepository = tokenDataRepository
            val getTokensResult = tokenDataRepository.getTokens()

            if (getTokensResult is ErrorResult) return@launch processError(getTokensResult.errorId)
            if (getTokensResult is InterruptionResult) return@launch processInterruption()

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
}