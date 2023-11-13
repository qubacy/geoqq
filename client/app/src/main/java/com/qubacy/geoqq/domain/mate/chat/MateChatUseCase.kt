package com.qubacy.geoqq.domain.mate.chat

import android.net.Uri
import com.qubacy.geoqq.data.common.message.model.DataMessage
import com.qubacy.geoqq.data.common.repository.common.result.common.Result
import com.qubacy.geoqq.data.common.repository.common.result.error.ErrorResult
import com.qubacy.geoqq.data.common.repository.common.result.interruption.InterruptionResult
import com.qubacy.geoqq.data.error.repository.ErrorDataRepository
import com.qubacy.geoqq.data.image.repository.ImageDataRepository
import com.qubacy.geoqq.data.image.repository.result.GetImageResult
import com.qubacy.geoqq.data.mate.message.repository.MateMessageDataRepository
import com.qubacy.geoqq.data.mate.message.repository.result.GetMessagesResult
import com.qubacy.geoqq.data.token.repository.TokenDataRepository
import com.qubacy.geoqq.data.token.repository.result.GetAccessTokenPayloadResult
import com.qubacy.geoqq.data.token.repository.result.GetTokensResult
import com.qubacy.geoqq.data.user.model.DataUser
import com.qubacy.geoqq.data.user.repository.UserDataRepository
import com.qubacy.geoqq.data.user.repository.result.GetUserByIdResult
import com.qubacy.geoqq.domain.common.model.User
import com.qubacy.geoqq.domain.common.model.message.Message
import com.qubacy.geoqq.domain.common.operation.common.Operation
import com.qubacy.geoqq.domain.common.usecase.consuming.ConsumingUseCase
import com.qubacy.geoqq.domain.mate.chat.state.MateChatState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MateChatUseCase(
    errorDataRepository: ErrorDataRepository,
    val tokenDataRepository: TokenDataRepository,
    val mateMessageDataRepository: MateMessageDataRepository,
    val imageDataRepository: ImageDataRepository,
    val userDataRepository: UserDataRepository,
) : ConsumingUseCase<MateChatState>(
    errorDataRepository, mateMessageDataRepository
) {
    companion object {
        const val USER_ID_TOKEN_PAYLOAD_KEY = "user-id"
    }

    private var mLocalUserId: Long = 0
    private var mInterlocutorUserId: Long = 0

    override suspend fun processResult(result: Result): Boolean {
        if (super.processResult(result)) return true

        when (result::class) {
            GetMessagesResult::class -> {
                val getMessagesResult = result as GetMessagesResult

                processGetMessagesResult(getMessagesResult)
            }
            else -> { return false }
        }

        return true
    }

    private suspend fun processGetMessagesResult(getMessagesResult: GetMessagesResult) {
        val localUser = getUser(mLocalUserId) ?: return
        val interlocutorUser = getUser(mInterlocutorUserId) ?: return

        val users = listOf(localUser, interlocutorUser)

        val mateMessages = mutableListOf<Message>()

        for (dataMessage in getMessagesResult.messages) {
            val message = processDataMessage(dataMessage, users) ?: return

            mateMessages.add(message)
        }

        val state = MateChatState(mateMessages)

        mStateFlow.emit(state)
    }

    private suspend fun getUser(userId: Long): User? {
        val dataUser = getDataUser(userId) ?: return null
        val userAvatarUri = getImageUri(dataUser.avatarId) ?: return null

        val user = User(
            dataUser.id, dataUser.username, dataUser.description, userAvatarUri, dataUser.isMate)

        return user
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

    private suspend fun processDataMessage(dataMessage: DataMessage, users: List<User>): Message? {
        val user = users.find { it.id == dataMessage.userId } ?: return null
        val message = Message(dataMessage.id, user, dataMessage.text, dataMessage.time)

        return message
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

    override fun generateState(operations: List<Operation>): MateChatState {
        return MateChatState(newOperations = operations)
    }

    private suspend fun getLocalUserId(): Long? {
        mCurrentRepository = tokenDataRepository
        val getLocalUserIdResult = tokenDataRepository.getAccessTokenPayload()

        if (getLocalUserIdResult is ErrorResult) {
            processError(getLocalUserIdResult.errorId)

            return null
        }
        if (getLocalUserIdResult is InterruptionResult) {
            processInterruption()

            return null
        }

        val payload = (getLocalUserIdResult as GetAccessTokenPayloadResult).payload

        return payload[USER_ID_TOKEN_PAYLOAD_KEY]?.asLong()
    }

    fun getChat(chatId: Long, interlocutorUserId: Long, count: Int) {
        mCoroutineScope.launch (Dispatchers.IO) {
            mCurrentRepository = tokenDataRepository
            val getTokensResult = tokenDataRepository.getTokens()

            if (getTokensResult is ErrorResult) return@launch processError(getTokensResult.errorId)
            if (getTokensResult is InterruptionResult) return@launch processInterruption()

            val getTokensResultCast = getTokensResult as GetTokensResult

            mCurrentRepository = tokenDataRepository
            mLocalUserId = getLocalUserId() ?: return@launch
            mInterlocutorUserId = interlocutorUserId

            mCurrentRepository = mateMessageDataRepository
            mateMessageDataRepository.getMessages(getTokensResultCast.accessToken, chatId, count)
        }
    }

    fun sendMessage(chatId: Long, messageText: String) {
        mCoroutineScope.launch (Dispatchers.IO) {
            mCurrentRepository = tokenDataRepository
            val getTokensResult = tokenDataRepository.getTokens()

            if (getTokensResult is ErrorResult) return@launch processError(getTokensResult.errorId)
            if (getTokensResult is InterruptionResult) return@launch processInterruption()

            val getTokensResultCast = getTokensResult as GetTokensResult

            mCurrentRepository = mateMessageDataRepository
            val sendMessageResult = mateMessageDataRepository.sendMessage(
                getTokensResultCast.accessToken, chatId, messageText)

            if (sendMessageResult is ErrorResult)
                return@launch processError(sendMessageResult.errorId)
            if (sendMessageResult is InterruptionResult) return@launch processInterruption()
        }
    }
}