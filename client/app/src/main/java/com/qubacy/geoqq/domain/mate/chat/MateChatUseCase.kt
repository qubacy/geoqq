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
import com.qubacy.geoqq.data.mate.request.repository.MateRequestDataRepository
import com.qubacy.geoqq.data.mate.request.repository.result.CreateMateRequestResult
import com.qubacy.geoqq.data.token.repository.TokenDataRepository
import com.qubacy.geoqq.data.token.repository.result.GetAccessTokenPayloadResult
import com.qubacy.geoqq.data.token.repository.result.GetTokensResult
import com.qubacy.geoqq.data.user.repository.UserDataRepository
import com.qubacy.geoqq.data.user.repository.result.GetUsersByIdsResult
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
import com.qubacy.geoqq.domain.common.usecase.util.extension.message.result.ProcessDataMessagesResult
import com.qubacy.geoqq.domain.mate.chat.result.ProcessGetMessagesResult
import com.qubacy.geoqq.domain.common.result.ProcessGetUserByIdResult
import com.qubacy.geoqq.domain.common.usecase.util.extension.message.MessageExtension
import com.qubacy.geoqq.domain.common.usecase.util.extension.user.result.GetUsersFromGetUsersByIdsResult
import com.qubacy.geoqq.domain.mate.chat.result.ApproveNewMateRequestCreationOperation
import com.qubacy.geoqq.domain.common.usecase.util.extension.user.result.ProcessDataUsersResult
import com.qubacy.geoqq.domain.mate.chat.state.MateChatState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MateChatUseCase(
    errorDataRepository: ErrorDataRepository,
    val tokenDataRepository: TokenDataRepository,
    val mateMessageDataRepository: MateMessageDataRepository,
    val imageDataRepository: ImageDataRepository,
    val userDataRepository: UserDataRepository,
    val mateRequestDataRepository: MateRequestDataRepository
) : ConsumingUseCase<MateChatState>(
        errorDataRepository,
        listOf(mateMessageDataRepository, userDataRepository)
), UserExtension, ImageExtension, TokenExtension, MessageExtension {
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
        val prevState = lockLastState()

        val getUsersFromGetUsersByIdsResult = getUsersFromGetUsersByIdsResult(
            getUsersByIdsResult, userDataRepository,
            imageDataRepository, tokenDataRepository, this)

        if (getUsersFromGetUsersByIdsResult is ErrorResult) return getUsersFromGetUsersByIdsResult

        val state = MateChatState(
            prevState?.messages ?: listOf(),
            (getUsersFromGetUsersByIdsResult as GetUsersFromGetUsersByIdsResult).users,
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

        val processDataMessagesResult = processDataMessages(getMessagesResult.messages)

        if (processDataMessagesResult is ErrorResult) return processDataMessagesResult

        val state = MateChatState(
            (processDataMessagesResult as ProcessDataMessagesResult).messages,
            getUsersResultCast.users,
            listOf(SetMessagesOperation())
        )

        Log.d(TAG, "processGetMessagesResult(): posting a state with messages.size = ${getMessagesResult.messages.size}")
        postState(state)

        return ProcessGetMessagesResult()
    }

    override fun generateState(operations: List<Operation>, prevState: MateChatState?): MateChatState {
        return MateChatState(
            prevState?.messages ?: listOf(),
            prevState?.users ?: listOf(),
            operations
        )
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
            val getAccessTokenResult = getAccessToken(tokenDataRepository)

            if (getAccessTokenResult is ErrorResult)
                return@launch processError(getAccessTokenResult.errorId)
            if (getAccessTokenResult is InterruptionResult) return@launch processInterruption()

            val getAccessTokenResultCast = getAccessTokenResult as GetAccessTokenResult

            mCurrentRepository = tokenDataRepository
            mLocalUserId = getLocalUserId() ?: return@launch //??
            mInterlocutorUserId = interlocutorUserId

            mCurrentRepository = mateMessageDataRepository
            mateMessageDataRepository.getMessages(getAccessTokenResultCast.accessToken, chatId, count)
        }
    }

    fun sendMessage(chatId: Long, messageText: String) {
        mCoroutineScope.launch (Dispatchers.IO) {
            mCurrentRepository = tokenDataRepository
            val getAccessTokenResult = getAccessToken(tokenDataRepository)

            if (getAccessTokenResult is ErrorResult)
                return@launch processError(getAccessTokenResult.errorId)
            if (getAccessTokenResult is InterruptionResult) return@launch processInterruption()

            val getAccessTokenResultCast = getAccessTokenResult as GetAccessTokenResult

            mCurrentRepository = mateMessageDataRepository
            val sendMessageResult = mateMessageDataRepository.sendMessage(
                getAccessTokenResultCast.accessToken, chatId, messageText)

            if (sendMessageResult is ErrorResult)
                return@launch processError(sendMessageResult.errorId)
            if (sendMessageResult is InterruptionResult) return@launch processInterruption()
        }
    }

    fun getInterlocutorUserDetails() {
        mCoroutineScope.launch (Dispatchers.IO) {
            // todo: is it ok??? we don't set the token rep. as a current one;
            mCurrentRepository = userDataRepository
            val getUsersResult = getUsers(
                listOf(mInterlocutorUserId), tokenDataRepository, this@MateChatUseCase,
                userDataRepository, imageDataRepository, this@MateChatUseCase,
                false, false
            )

            if (getUsersResult is ErrorResult) return@launch processError(getUsersResult.errorId)
            if (getUsersResult is InterruptionResult) return@launch processInterruption()

            val getUsersResultCast = getUsersResult as GetUsersResult

            val prevState = lockLastState()
            val updatedInterlocutorUser = getUsersResultCast.users.find { user -> user.id == mInterlocutorUserId }!!  // todo: delete this!!!

            var isInterlocutorUserUpdated = false
            val updatedUsers = prevState?.users?.map { prevUser ->
                if (prevUser.id == mInterlocutorUserId) {
                    if (prevUser != updatedInterlocutorUser) isInterlocutorUserUpdated = true

                    updatedInterlocutorUser
                    // todo: change to: getUsersResultCast.users.first()
                }
                else prevUser
            }

            val state = MateChatState(
                prevState?.messages ?: listOf(),
                updatedUsers ?: listOf(),
                listOf(SetUsersDetailsOperation(listOf(mInterlocutorUserId), isInterlocutorUserUpdated))
            )

            postState(state)
        }
    }

    fun createMateRequest(userId: Long) {
        mCoroutineScope.launch(Dispatchers.IO) {
            mCurrentRepository = tokenDataRepository
            val getAccessTokenResult = getAccessToken(tokenDataRepository)

            if (getAccessTokenResult is ErrorResult)
                return@launch processError(getAccessTokenResult.errorId)
            if (getAccessTokenResult is InterruptionResult) return@launch processInterruption()

            val getAccessTokenResultCast = getAccessTokenResult as GetAccessTokenResult

            mCurrentRepository = mateRequestDataRepository
            val createMateRequestResult = mateRequestDataRepository.createMateRequest(
                getAccessTokenResultCast.accessToken, userId)

            if (createMateRequestResult is ErrorResult)
                return@launch processError(createMateRequestResult.errorId)
            if (createMateRequestResult is InterruptionResult)
                return@launch processInterruption()

            val createMateRequestResultCast = createMateRequestResult as CreateMateRequestResult

            val prevState = lockLastState()
            val newState = generateState(listOf(ApproveNewMateRequestCreationOperation()), prevState)

            postState(newState)
        }
    }
}