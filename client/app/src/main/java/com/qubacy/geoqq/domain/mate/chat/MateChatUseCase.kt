package com.qubacy.geoqq.domain.mate.chat

import android.util.Log
import com.qubacy.geoqq.data.common.message.model.DataMessage
import com.qubacy.geoqq.data.common.repository.common.result.common.Result
import com.qubacy.geoqq.data.common.repository.common.result.error.ErrorResult
import com.qubacy.geoqq.data.common.repository.common.result.interruption.InterruptionResult
import com.qubacy.geoqq.data.error.repository.ErrorDataRepository
import com.qubacy.geoqq.data.image.repository.ImageDataRepository
import com.qubacy.geoqq.data.mate.message.repository.MateMessageDataRepository
import com.qubacy.geoqq.data.mate.message.repository.result.GetMessagesResult
import com.qubacy.geoqq.data.token.repository.TokenDataRepository
import com.qubacy.geoqq.data.token.repository.result.GetAccessTokenPayloadResult
import com.qubacy.geoqq.data.token.repository.result.GetTokensResult
import com.qubacy.geoqq.data.user.repository.UserDataRepository
import com.qubacy.geoqq.data.user.repository.result.GetUsersByIdsResult
import com.qubacy.geoqq.domain.common.model.User
import com.qubacy.geoqq.domain.common.model.message.Message
import com.qubacy.geoqq.domain.common.operation.common.Operation
import com.qubacy.geoqq.domain.common.usecase.consuming.ConsumingUseCase
import com.qubacy.geoqq.domain.common.usecase.util.extension.image.ImageExtension
import com.qubacy.geoqq.domain.common.usecase.util.extension.token.TokenExtension
import com.qubacy.geoqq.domain.common.usecase.util.extension.token.result.GetAccessTokenResult
import com.qubacy.geoqq.domain.common.usecase.util.extension.user.UserExtension
import com.qubacy.geoqq.domain.common.usecase.util.extension.user.result.GetUsersResult
import com.qubacy.geoqq.domain.mate.chat.operation.SetMessagesOperation
import com.qubacy.geoqq.domain.common.operation.chat.SetUsersDetailsOperation
import com.qubacy.geoqq.domain.mate.chat.result.ProcessDataMessageResult
import com.qubacy.geoqq.domain.mate.chat.result.ProcessGetMessagesResult
import com.qubacy.geoqq.domain.common.result.ProcessGetUserByIdResult
import com.qubacy.geoqq.domain.common.usecase.util.extension.user.result.GetUsersAvatarUrisResult
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
        errorDataRepository,
        listOf(mateMessageDataRepository, userDataRepository)
), UserExtension, ImageExtension, TokenExtension {
    companion object {
        const val TAG = "MateChatUseCase"

        const val USER_ID_TOKEN_PAYLOAD_KEY = "user-id"
    }

    private var mLocalUserId: Long = 0
    private var mInterlocutorUserId: Long = 0

    override suspend fun processResult(result: Result): Boolean {
        if (super.processResult(result)) return true

        val result = when (result::class) {
            GetMessagesResult::class -> {
                val getMessagesResult = result as GetMessagesResult

                processGetMessagesResult(getMessagesResult)
            }
            GetUsersByIdsResult::class -> {
                val getUsersByIdsResult = result as GetUsersByIdsResult

                processGetUsersByIdsResult(getUsersByIdsResult)
            }
            else -> { return false }
        }

        if (result is ErrorResult) processError(result.errorId)

        return true
    }

    private suspend fun processGetUsersByIdsResult(
        getUsersByIdsResult: GetUsersByIdsResult
    ): Result {
        val getAccessTokenResult = getAccessToken(tokenDataRepository)

        if (getAccessTokenResult is ErrorResult) return getAccessTokenResult

        val getAccessTokenResultCast = getAccessTokenResult as GetAccessTokenResult

        Log.d(TAG, "processGetUserByIdResult(): before posting a state with users.size = ${getUsersByIdsResult.users.size}; areLocal = ${getUsersByIdsResult.areLocal}")

        val prevState = lockLastState()

        val getUsersAvatarUrisResult = getUsersAvatarUris(
            getUsersByIdsResult.users,
            getAccessTokenResultCast.accessToken,
            userDataRepository,
            imageDataRepository
        )

        if (getUsersAvatarUrisResult is ErrorResult) return getUsersAvatarUrisResult

        val getUsersAvatarUrisResultCast = getUsersAvatarUrisResult as GetUsersAvatarUrisResult

        val updatedUsers = getUsersByIdsResult.users.map {
            User(
                it.id,
                it.username,
                it.description,
                getUsersAvatarUrisResultCast.avatarUrisMap[it.id]!!,
                it.isMate
            )
        }

        val state = MateChatState(
            prevState?.messages ?: listOf(),
            updatedUsers,
            listOf(
                SetUsersDetailsOperation(
                    getUsersByIdsResult.users.map { it.id }, !getUsersByIdsResult.areLocal)
            )
        )

        Log.d(TAG, "processGetUserByIdResult(): posting a state with users.size = ${getUsersByIdsResult.users.size}; areLocal = ${getUsersByIdsResult.areLocal}")

        postState(state)

        return ProcessGetUserByIdResult()
    }

    private suspend fun processGetMessagesResult(getMessagesResult: GetMessagesResult): Result {
        Log.d(TAG, "processGetMessagesResult(): before posting a state with messages.size = ${getMessagesResult.messages.size}")

        lockLastState()

        val getAccessTokenResult = getAccessToken(tokenDataRepository)

        if (getAccessTokenResult is ErrorResult) return getAccessTokenResult

        val getAccessTokenResultCast = getAccessTokenResult as GetAccessTokenResult

        val getUsersResult = getUsers(
            listOf(mLocalUserId, mInterlocutorUserId),
            getAccessTokenResultCast.accessToken,
            userDataRepository,
            imageDataRepository,
            this
        )

        if (getUsersResult is ErrorResult) return getUsersResult

        val getUsersResultCast = getUsersResult as GetUsersResult

        val mateMessages = mutableListOf<Message>()

        for (dataMessage in getMessagesResult.messages) {
            val processDataMessageResult = processDataMessage(dataMessage, getUsersResultCast.users)

            if (processDataMessageResult is ErrorResult) return processDataMessageResult

            mateMessages.add((processDataMessageResult as ProcessDataMessageResult).message)
        }

        val state = MateChatState(
            mateMessages, getUsersResultCast.users, listOf(SetMessagesOperation())
        )

        Log.d(TAG, "processGetMessagesResult(): posting a state with messages.size = ${getMessagesResult.messages.size}")
        postState(state)

        return ProcessGetMessagesResult()
    }

    private fun processDataMessage(dataMessage: DataMessage, users: List<User>): Result {
        Log.d(TAG, "processDataMessage(): dataMessage.id = ${dataMessage.id}; dataMessage.userId = ${dataMessage.userId}")

        val user = users.find { it.id == dataMessage.userId }!!
        val message = Message(dataMessage.id, user.id, dataMessage.text, dataMessage.time)

        return ProcessDataMessageResult(message)
    }

    override fun generateState(operations: List<Operation>): MateChatState {
        return MateChatState(newOperations = operations)
    }

    private fun getLocalUserId(): Long? {
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
            mLocalUserId = getLocalUserId() ?: return@launch //??
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

    fun getInterlocutorUserDetails() {
        mCoroutineScope.launch (Dispatchers.IO) {
            mCurrentRepository = tokenDataRepository
            val getTokensResult = tokenDataRepository.getTokens()

            if (getTokensResult is ErrorResult) return@launch processError(getTokensResult.errorId)
            if (getTokensResult is InterruptionResult) return@launch processInterruption()

            val getTokensResultCast = getTokensResult as GetTokensResult

            mCurrentRepository = userDataRepository
            val getUsersByIdsResult = userDataRepository.getUsersByIds(
                listOf(mInterlocutorUserId), getTokensResultCast.accessToken,
                false, false
            )

            if (getUsersByIdsResult is ErrorResult) return@launch processError(getUsersByIdsResult.errorId)
            if (getUsersByIdsResult is InterruptionResult) return@launch processInterruption()

            val getUsersByIdsResultCast = getUsersByIdsResult as GetUsersByIdsResult

//            val prevState = lockLastState()
//            val updatedUsers = prevState?.users?.map {
//                if (it.id == mInterlocutorUserId) getUserByIdResult
//            }
//
//            val state = MateChatState(
//                prevState?.messages ?: listOf(),
//                prevState?.users?.map { it.id == mInterlocutorUserId } ?: listOf(),
//                listOf(SetUsersDetailsOperation(listOf(mInterlocutorUserId), false))
//            )
//
//            postState(state)
        }
    }
}