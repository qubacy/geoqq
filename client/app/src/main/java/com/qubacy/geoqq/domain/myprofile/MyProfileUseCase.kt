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
import com.qubacy.geoqq.data.token.repository.TokenDataRepository
import com.qubacy.geoqq.data.token.repository.result.GetTokensResult
import com.qubacy.geoqq.domain.common.UseCase
import com.qubacy.geoqq.domain.myprofile.operation.SuccessfulProfileSavingCallbackOperation
import com.qubacy.geoqq.domain.myprofile.state.MyProfileState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class MyProfileUseCase(
    errorDataRepository: ErrorDataRepository,
    val tokenDataRepository: TokenDataRepository,
    val myProfileDataRepository: MyProfileDataRepository,
    val imageDataRepository: ImageDataRepository
) : UseCase<MyProfileState>(errorDataRepository) {
    private lateinit var mOriginalMyProfileRepositoryFlowJob: Job

    init {
        startMyProfileRepositoryFlowCollection()
    }

    private fun startMyProfileRepositoryFlowCollection() {
        mOriginalMyProfileRepositoryFlowJob = mCoroutineScope.launch(Dispatchers.IO) {
            myProfileDataRepository.resultFlow.collect {
                processResult(it)
            }
        }
    }

    override fun setCoroutineScope(coroutineScope: CoroutineScope) {
        super.setCoroutineScope(coroutineScope)

        mOriginalMyProfileRepositoryFlowJob.cancel()
        startMyProfileRepositoryFlowCollection()
    }

    private suspend fun processResult(result: Result) {
        when (result::class) {
            GetMyProfileResult::class -> {
                val getMyProfileResult = result as GetMyProfileResult

                processGetMyProfileResult(getMyProfileResult)
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
        mCurrentRepository = tokenDataRepository
        val getTokensResult = tokenDataRepository.getTokens()

        if (getTokensResult is ErrorResult) return processError(getTokensResult.errorId)
        if (getTokensResult is InterruptionResult) return processInterruption()

        val accessToken = (getTokensResult as GetTokensResult).accessToken

        mCurrentRepository = imageDataRepository
        val getImageResult = imageDataRepository.getImage(
            myProfileData.avatarId, accessToken)

        if (getImageResult is ErrorResult) return processError(getImageResult.errorId)
        if (getImageResult is InterruptionResult) return processInterruption()

        val getImageResultCast = getImageResult as GetImageResult

        val dataMyProfile = DataMyProfileWithLinkedAvatar(
            myProfileData.username,
            myProfileData.description,
            myProfileData.hitUpOption,
            getImageResultCast.imageUri
        )
        mCurrentRepository = myProfileDataRepository
        val saveMyProfileResult = myProfileDataRepository.saveMyProfile(dataMyProfile)

        if (saveMyProfileResult is ErrorResult) return processError(saveMyProfileResult.errorId)

        val state = MyProfileState(
            dataMyProfile.avatarUri,
            dataMyProfile.username,
            dataMyProfile.description,
            dataMyProfile.hitUpOption,
            listOf()
        )

        mStateFlow.emit(state)
    }

    private suspend fun processGetMyProfileResultWithLinkedAvatar(
        myProfileData: DataMyProfileWithLinkedAvatar
    ) {
        val state = MyProfileState(
            myProfileData.avatarUri,
            myProfileData.username,
            myProfileData.description,
            myProfileData.hitUpOption,
            listOf()
        )

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

    private suspend fun changeCurrentStateAfterUpdate(
        avatarUri: Uri?,
        description: String?,
        hitUpOption: MyProfileDataModelContext.HitUpOption?
    ) {
        val state = stateFlow.value
        val newState = MyProfileState(
            avatarUri ?: (state?.avatar ?: Uri.parse(String())),
            state?.username ?: String(),
            description ?: (state?.description ?: String()),
            hitUpOption ?:
            (state?.hitUpOption ?: MyProfileDataModelContext.HitUpOption.POSITIVE),
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
            return processError(saveMyProfileResult.errorId)

        mStateFlow.emit(newState)
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