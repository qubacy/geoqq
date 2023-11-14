package com.qubacy.geoqq.domain.mate.chats

import android.net.Uri
import com.qubacy.geoqq.domain.common.operation.common.Operation
import com.qubacy.geoqq.data.common.repository.common.result.common.Result
import com.qubacy.geoqq.data.common.repository.common.result.error.ErrorResult
import com.qubacy.geoqq.data.common.repository.common.result.interruption.InterruptionResult
import com.qubacy.geoqq.data.error.repository.ErrorDataRepository
import com.qubacy.geoqq.data.image.repository.ImageDataRepository
import com.qubacy.geoqq.data.image.repository.result.GetImageResult
import com.qubacy.geoqq.data.mate.chat.model.DataMateChat
import com.qubacy.geoqq.data.mate.chat.repository.MateChatDataRepository
import com.qubacy.geoqq.data.mate.chat.repository.result.GetChatsResult
import com.qubacy.geoqq.data.mate.request.repository.MateRequestDataRepository
import com.qubacy.geoqq.data.mate.request.repository.result.GetMateRequestCountResult
import com.qubacy.geoqq.data.token.repository.TokenDataRepository
import com.qubacy.geoqq.data.token.repository.result.GetTokensResult
import com.qubacy.geoqq.data.user.model.DataUser
import com.qubacy.geoqq.data.user.repository.UserDataRepository
import com.qubacy.geoqq.data.user.repository.result.GetUserByIdResult
import com.qubacy.geoqq.domain.common.model.message.common.MessageBase
import com.qubacy.geoqq.domain.common.usecase.consuming.ConsumingUseCase
import com.qubacy.geoqq.domain.mate.chats.model.MateChat
import com.qubacy.geoqq.domain.mate.chats.state.MateChatsState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MateChatsUseCase(
    errorDataRepository: ErrorDataRepository,
    val tokenDataRepository: TokenDataRepository,
    val mateChatDataRepository: MateChatDataRepository,
    val imageDataRepository: ImageDataRepository,
    val userDataRepository: UserDataRepository,
    val mateRequestDataRepository: MateRequestDataRepository
) : ConsumingUseCase<MateChatsState>(
    errorDataRepository,
    listOf(mateChatDataRepository, userDataRepository, mateRequestDataRepository)
) {
    override suspend fun processResult(result: Result): Boolean {
        if (super.processResult(result)) return true

        when (result::class) {
            GetChatsResult::class -> {
                val getChatsResult = result as GetChatsResult

                processGetChatsResult(getChatsResult)
            }
            else -> { return false }
        }

        return true
    }

    private suspend fun processGetChatsResult(result: GetChatsResult) {
        val mateChats = mutableListOf<MateChat>()

        for (dataChat in result.chats) {
            val mateChat = processDataMateChat(dataChat) ?: return

            mateChats.add(mateChat)
        }

        val mateRequestCount = getMateRequestCount() ?: return // todo: is this legal?
        val newState = MateChatsState(mateChats, mateRequestCount)

        mStateFlow.emit(newState)
    }

    private suspend fun getMateRequestCount(): Int? {
        val accessToken = getAccessToken() ?: return null
        val getRequestCountResult = mateRequestDataRepository.getMateRequestCount(accessToken)

        if (getRequestCountResult is ErrorResult) {
            processError(getRequestCountResult.errorId)

            return null
        }
        if (getRequestCountResult is InterruptionResult) {
            processInterruption()

            return null
        }

        return (getRequestCountResult as GetMateRequestCountResult).mateRequestCount
    }

    private suspend fun processDataMateChat(dataMateChat: DataMateChat): MateChat? {
        val interlocutorDataUser = getDataUser(dataMateChat.userId) ?: return null
        val interlocutorDataUserAvatarUri = getImageUri(interlocutorDataUser.avatarId) ?: return null

        val lastMessage = if (dataMateChat.lastMessage != null)
            MessageBase(dataMateChat.lastMessage.text, dataMateChat.lastMessage.time)
        else
            null

        return MateChat(
            dataMateChat.id, dataMateChat.userId, interlocutorDataUserAvatarUri,
            interlocutorDataUser.username, lastMessage)
    }

    private suspend fun getAccessToken(): String? {
        mCurrentRepository = tokenDataRepository
        val getTokensResult = tokenDataRepository.getTokens()

        if (getTokensResult is ErrorResult) {
            processError(getTokensResult.errorId)

            return null
        }
        if (getTokensResult is InterruptionResult) {
            processInterruption()

            return null
        }

        val getTokensResultCast = getTokensResult as GetTokensResult

        return getTokensResultCast.accessToken
    }

    private suspend fun getDataUser(userId: Long): DataUser? {
        val accessToken = getAccessToken() ?: return null

        mCurrentRepository = userDataRepository
        val getUserResult = userDataRepository.getUserById(
            userId, accessToken)

        if (getUserResult is ErrorResult) {
            processError(getUserResult.errorId)

            return null
        }
        if (getUserResult is InterruptionResult) {
            processInterruption()

            return null
        }

        val getUserResultCast = getUserResult as GetUserByIdResult

        return getUserResultCast.user
    }

    private suspend fun getImageUri(imageId: Long): Uri? {
        val accessToken = getAccessToken() ?: return null

        mCurrentRepository = imageDataRepository
        val getImageResult = imageDataRepository.getImage(
            imageId, accessToken)

        if (getImageResult is ErrorResult) {
            processError(getImageResult.errorId)

            return null
        }
        if (getImageResult is InterruptionResult) {
            processInterruption()

            return null
        }

        val getImageResultCast = getImageResult as GetImageResult

        return getImageResultCast.imageUri
    }

    override fun generateState(operations: List<Operation>): MateChatsState {
        return MateChatsState(newOperations = operations)
    }

    fun getMateChats(count: Int) {
        mCoroutineScope.launch(Dispatchers.IO) {
            mCurrentRepository = tokenDataRepository
            val getTokensResult = tokenDataRepository.getTokens()

            if (getTokensResult is ErrorResult) return@launch processError(getTokensResult.errorId)
            if (getTokensResult is InterruptionResult) return@launch processInterruption()

            val getTokensResultCast = getTokensResult as GetTokensResult

            mCurrentRepository = mateChatDataRepository
            mateChatDataRepository.getChats(getTokensResultCast.accessToken, count)
        }
    }
}