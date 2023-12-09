package com.qubacy.geoqq.domain.myprofile

import android.graphics.Bitmap
import android.net.Uri
import com.qubacy.geoqq.domain.common.operation.common.Operation
import com.qubacy.geoqq.data.common.repository.common.result.common.Result
import com.qubacy.geoqq.data.common.repository.common.result.error.ErrorResult
import com.qubacy.geoqq.data.common.repository.common.result.interruption.InterruptionResult
import com.qubacy.geoqq.data.error.repository.ErrorDataRepository
import com.qubacy.geoqq.data.image.repository.ImageDataRepository
import com.qubacy.geoqq.data.image.repository.result.GetImageByUriResult
import com.qubacy.geoqq.data.image.repository.result.GetImagesResult
import com.qubacy.geoqq.data.myprofile.model.avatar.labeled.DataMyProfileWithAvatarId
import com.qubacy.geoqq.data.myprofile.model.avatar.linked.DataMyProfileWithLinkedAvatar
import com.qubacy.geoqq.data.myprofile.model.common.DataMyProfile
import com.qubacy.geoqq.data.myprofile.repository.MyProfileDataRepository
import com.qubacy.geoqq.data.myprofile.repository.result.GetMyProfileResult
import com.qubacy.geoqq.data.token.repository.TokenDataRepository
import com.qubacy.geoqq.data.token.repository.result.GetTokensResult
import com.qubacy.geoqq.domain.common.usecase.consuming.ConsumingUseCase
import com.qubacy.geoqq.domain.myprofile.operation.SetProfileDataOperation
import com.qubacy.geoqq.domain.myprofile.operation.SuccessfulProfileSavingCallbackOperation
import com.qubacy.geoqq.domain.myprofile.state.MyProfileState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

open class MyProfileUseCase(
    errorDataRepository: ErrorDataRepository,
    val tokenDataRepository: TokenDataRepository,
    val myProfileDataRepository: MyProfileDataRepository,
    val imageDataRepository: ImageDataRepository
) : ConsumingUseCase<MyProfileState>(
    errorDataRepository,
    listOf(myProfileDataRepository)
) {
    override suspend fun processResult(result: Result): Boolean {
        if (super.processResult(result)) return true

        when (result::class) {
            GetMyProfileResult::class -> {
                val getMyProfileResult = result as GetMyProfileResult

                processGetMyProfileResult(getMyProfileResult)
            }
            else -> { return false }
        }

        return true
    }

    private suspend fun processGetMyProfileResult(getMyProfileResult: GetMyProfileResult) {
        if (getMyProfileResult.myProfileData is DataMyProfileWithAvatarId) {
            processGetMyProfileResultWithAvatarId(getMyProfileResult.myProfileData)

        } else if (getMyProfileResult.myProfileData is DataMyProfileWithLinkedAvatar) {
            processGetMyProfileResultWithLinkedAvatar(getMyProfileResult.myProfileData)

        } else
            throw IllegalStateException()
    }

    private suspend fun processGetMyProfileResultWithAvatarId(
        myProfileData: DataMyProfileWithAvatarId
    ) {
        lockLastState()

        mCurrentRepository = tokenDataRepository
        val getTokensResult = tokenDataRepository.getTokens()

        if (getTokensResult is ErrorResult) return processError(getTokensResult.errorId)
        if (getTokensResult is InterruptionResult) return processInterruption()

        val accessToken = (getTokensResult as GetTokensResult).accessToken

        mCurrentRepository = imageDataRepository
        val getImagesResult = imageDataRepository.getImages(
            listOf(myProfileData.avatarId), accessToken)

        if (getImagesResult is ErrorResult) return processError(getImagesResult.errorId)
        if (getImagesResult is InterruptionResult) return processInterruption()

        val getImagesResultCast = getImagesResult as GetImagesResult

        val dataMyProfile = DataMyProfileWithLinkedAvatar(
            myProfileData.username,
            myProfileData.description,
            myProfileData.hitUpOption,
            getImagesResultCast.imageIdToUriMap[myProfileData.avatarId]!!
        )
        mCurrentRepository = myProfileDataRepository
        val saveMyProfileResult = myProfileDataRepository.saveMyProfile(dataMyProfile)

        if (saveMyProfileResult is ErrorResult) return processError(saveMyProfileResult.errorId)

        val state = MyProfileState(
            dataMyProfile.avatarUri,
            dataMyProfile.username,
            dataMyProfile.description,
            dataMyProfile.hitUpOption,
            listOf(SetProfileDataOperation())
        )

        postState(state)
    }

    private suspend fun processGetMyProfileResultWithLinkedAvatar(
        myProfileData: DataMyProfileWithLinkedAvatar
    ) {
        lockLastState()

        val state = MyProfileState(
            myProfileData.avatarUri,
            myProfileData.username,
            myProfileData.description,
            myProfileData.hitUpOption,
            listOf(SetProfileDataOperation())
        )

        postState(state)
    }

    override fun generateState(
        operations: List<Operation>,
        prevState: MyProfileState?
    ): MyProfileState {
        return MyProfileState(
            prevState?.avatar ?: Uri.parse(String()),
            prevState?.username ?: String(),
            prevState?.description ?: String(),
            prevState?.hitUpOption ?: DataMyProfile.HitUpOption.POSITIVE,
            operations
        )
    }

    private suspend fun changeCurrentStateAfterUpdate(
        avatarUri: Uri?,
        description: String?,
        hitUpOption: DataMyProfile.HitUpOption?
    ) {
        val prevState = lockLastState()
        val newState = MyProfileState(
            avatarUri ?: (prevState?.avatar ?: Uri.parse(String())),
            prevState?.username ?: String(),
            description ?: (prevState?.description ?: String()),
            hitUpOption ?:
            (prevState?.hitUpOption ?: DataMyProfile.HitUpOption.POSITIVE),
            listOf(SuccessfulProfileSavingCallbackOperation())
        )

        val dataMyProfile = DataMyProfileWithLinkedAvatar(
            newState.username,
            newState.description,
            newState.hitUpOption,
            newState.avatar
        )
        mCurrentRepository = myProfileDataRepository
        val saveMyProfileResult = myProfileDataRepository.saveMyProfile(dataMyProfile)

        if (saveMyProfileResult is ErrorResult)
            return processError(saveMyProfileResult.errorId, prevState)

        postState(newState)
    }

    open fun getMyProfile() {
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

    open fun updateMyProfile(
        avatarUri: Uri?,
        description: String?,
        password: String?,
        newPassword: String?,
        hitUpOption: DataMyProfile.HitUpOption?
    ) {
        mCoroutineScope.launch(Dispatchers.IO) {
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
            val updateMyProfileResult = myProfileDataRepository.updateMyProfile(
                accessToken,
                avatarBitmap,
                description,
                password,
                newPassword,
                hitUpOption
            )

            if (updateMyProfileResult is ErrorResult)
                return@launch processError(updateMyProfileResult.errorId)
            if (updateMyProfileResult is InterruptionResult) return@launch processInterruption()

            changeCurrentStateAfterUpdate(avatarUri, description, hitUpOption)
        }
    }
}