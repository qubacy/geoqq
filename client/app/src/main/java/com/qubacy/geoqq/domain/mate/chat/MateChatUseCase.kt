package com.qubacy.geoqq.domain.mate.chat

import com.qubacy.geoqq.data.common.repository.common.result.common.Result
import com.qubacy.geoqq.data.common.repository.common.result.error.ErrorResult
import com.qubacy.geoqq.data.common.repository.common.result.interruption.InterruptionResult
import com.qubacy.geoqq.data.error.repository.ErrorDataRepository
import com.qubacy.geoqq.data.image.repository.ImageDataRepository
import com.qubacy.geoqq.data.mate.message.repository.MateMessageDataRepository
import com.qubacy.geoqq.data.mate.message.repository.result.GetMessagesResult
import com.qubacy.geoqq.data.mate.request.repository.MateRequestDataRepository
import com.qubacy.geoqq.data.token.repository.TokenDataRepository
import com.qubacy.geoqq.data.user.repository.UserDataRepository
import com.qubacy.geoqq.domain.common.model.User
import com.qubacy.geoqq.domain.common.model.message.Message
import com.qubacy.geoqq.domain.common.operation.common.Operation
import com.qubacy.geoqq.domain.common.usecase.util.extension.image.ImageExtension
import com.qubacy.geoqq.domain.common.usecase.util.extension.token.TokenExtension
import com.qubacy.geoqq.domain.common.usecase.util.extension.token.result.GetAccessTokenResult
import com.qubacy.geoqq.domain.common.usecase.util.extension.user.UserExtension
import com.qubacy.geoqq.domain.common.usecase.util.extension.user.result.GetUsersResult
import com.qubacy.geoqq.domain.mate.chat.operation.SetMessagesOperation
import com.qubacy.geoqq.domain.common.usecase.util.extension.message.result.ProcessDataMessagesResult
import com.qubacy.geoqq.domain.mate.chat.result.ProcessGetMessagesResult
import com.qubacy.geoqq.domain.common.usecase.chat.ChatUseCase
import com.qubacy.geoqq.domain.common.usecase.util.extension.message.MessageExtension
import com.qubacy.geoqq.domain.common.usecase.util.extension.user.result.GetLocalUserIdResult
import com.qubacy.geoqq.domain.mate.chat.operation.AddPrecedingMessagesOperation
import com.qubacy.geoqq.domain.mate.chat.state.MateChatState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

open class MateChatUseCase(
    errorDataRepository: ErrorDataRepository,
    tokenDataRepository: TokenDataRepository,
    val mateMessageDataRepository: MateMessageDataRepository,
    imageDataRepository: ImageDataRepository,
    userDataRepository: UserDataRepository,
    mateRequestDataRepository: MateRequestDataRepository
) : ChatUseCase<MateChatState>(
        errorDataRepository,
        tokenDataRepository,
        imageDataRepository,
        userDataRepository,
        mateRequestDataRepository,
        listOf(mateMessageDataRepository)
), UserExtension, ImageExtension, TokenExtension, MessageExtension {
    companion object {
        const val TAG = "MateChatUseCase"
    }

    private var mLocalUserId: Long = 0
    private var mInterlocutorUserId: Long = 0

    private var mLastPrecedingMessages: List<Message>? = null

    override suspend fun processResult(result: Result): Boolean {
        return super.processResult(result)
    }

    override suspend fun processGetMessagesResult(getMessagesResult: GetMessagesResult): Result {
        val prevState = lockLastState()

        val getAccessTokenResult = getAccessToken(tokenDataRepository)

        if (getAccessTokenResult is ErrorResult) return getAccessTokenResult

        val getAccessTokenResultCast = getAccessTokenResult as GetAccessTokenResult

        val getUsersResult = getUsers(
            listOf(mLocalUserId, mInterlocutorUserId),
            getAccessTokenResultCast.accessToken,
            userDataRepository,
            imageDataRepository,
            this,
            !getMessagesResult.areLocal
        )

        if (getUsersResult is ErrorResult) return getUsersResult

        val getUsersResultCast = getUsersResult as GetUsersResult

        val processDataMessagesResult = processDataMessages(getMessagesResult.messages)

        if (processDataMessagesResult is ErrorResult) return processDataMessagesResult

        val processDataMessagesResultCast = processDataMessagesResult as ProcessDataMessagesResult

        val messages = if (getMessagesResult.isInitial) {
            processDataMessagesResultCast.messages
        } else {
            if (getMessagesResult.areLocal) {
                val newMessages = prevState!!.messages + processDataMessagesResultCast.messages

                mLastPrecedingMessages = processDataMessagesResultCast.messages
                newMessages
            } else {
                val newMessages = if (mLastPrecedingMessages == null)
                    prevState!!.messages + processDataMessagesResultCast.messages
                else {
                    prevState!!.messages
                        .subList(0, prevState.messages.size - mLastPrecedingMessages!!.size) +
                            processDataMessagesResultCast.messages
                }

                mLastPrecedingMessages = null
                newMessages
            }
        }
        val operations = if (getMessagesResult.isInitial) listOf(SetMessagesOperation()) else
            listOf(AddPrecedingMessagesOperation(
                processDataMessagesResultCast.messages, !getMessagesResult.areLocal))

        val state = MateChatState(
            messages,
            getUsersResultCast.users,
            operations
        )

        postState(state)

        return ProcessGetMessagesResult()
    }

    override fun generateChatState(
        messages: List<Message>,
        users: List<User>,
        operations: List<Operation>
    ): MateChatState {
        return MateChatState(messages, users, operations)
    }

    override fun generateState(
        operations: List<Operation>, prevState: MateChatState?
    ): MateChatState {
        return MateChatState(
            prevState?.messages ?: listOf(),
            prevState?.users ?: listOf(),
            operations
        )
    }

    open fun getChat(chatId: Long, interlocutorUserId: Long, count: Int) {
        mCoroutineScope.launch (Dispatchers.IO) {
            mCurrentRepository = tokenDataRepository
            val getAccessTokenResult = getAccessToken(tokenDataRepository)

            if (getAccessTokenResult is ErrorResult)
                return@launch processError(getAccessTokenResult.errorId)
            if (getAccessTokenResult is InterruptionResult) return@launch processInterruption()

            val getAccessTokenResultCast = getAccessTokenResult as GetAccessTokenResult

            mCurrentRepository = tokenDataRepository
            val getLocalUserIdResult = getLocalUserId(tokenDataRepository)

            if (getLocalUserIdResult is ErrorResult)
                return@launch processError(getLocalUserIdResult.errorId)
            if (getLocalUserIdResult is InterruptionResult) return@launch processInterruption()

            mLocalUserId = (getLocalUserIdResult as GetLocalUserIdResult).localUserId
            mInterlocutorUserId = interlocutorUserId

            mCurrentRepository = mateMessageDataRepository
            mateMessageDataRepository.getMessages(getAccessTokenResultCast.accessToken, chatId, count)
        }
    }

    open fun sendMessage(chatId: Long, messageText: String) {
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

    open fun getInterlocutorUserDetails() {
        getUserDetails(mInterlocutorUserId)
    }
}