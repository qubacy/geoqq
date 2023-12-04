package com.qubacy.geoqq.domain.common.usecase.chat

import com.qubacy.geoqq.data.common.repository.common.result.common.Result
import com.qubacy.geoqq.data.common.repository.common.result.error.ErrorResult
import com.qubacy.geoqq.data.common.repository.common.result.interruption.InterruptionResult
import com.qubacy.geoqq.data.common.repository.network.flowable.FlowableDataRepository
import com.qubacy.geoqq.data.error.repository.ErrorDataRepository
import com.qubacy.geoqq.data.image.repository.ImageDataRepository
import com.qubacy.geoqq.data.mate.message.repository.result.GetMessagesResult
import com.qubacy.geoqq.data.mate.request.repository.MateRequestDataRepository
import com.qubacy.geoqq.data.mate.request.repository.result.CreateMateRequestResult
import com.qubacy.geoqq.data.token.repository.TokenDataRepository
import com.qubacy.geoqq.data.user.repository.UserDataRepository
import com.qubacy.geoqq.data.user.repository.result.GetUsersByIdsResult
import com.qubacy.geoqq.domain.common.model.User
import com.qubacy.geoqq.domain.common.model.message.Message
import com.qubacy.geoqq.domain.common.operation.chat.SetUsersDetailsOperation
import com.qubacy.geoqq.domain.common.operation.common.Operation
import com.qubacy.geoqq.domain.common.result.ProcessGetUserByIdResult
import com.qubacy.geoqq.domain.common.state.chat.ChatState
import com.qubacy.geoqq.domain.common.usecase.consuming.ConsumingUseCase
import com.qubacy.geoqq.domain.common.usecase.util.extension.image.ImageExtension
import com.qubacy.geoqq.domain.common.usecase.util.extension.message.MessageExtension
import com.qubacy.geoqq.domain.common.usecase.util.extension.token.TokenExtension
import com.qubacy.geoqq.domain.common.usecase.util.extension.token.result.GetAccessTokenResult
import com.qubacy.geoqq.domain.common.usecase.util.extension.user.UserExtension
import com.qubacy.geoqq.domain.common.usecase.util.extension.user.result.GetUsersFromGetUsersByIdsResult
import com.qubacy.geoqq.domain.common.usecase.util.extension.user.result.GetUsersResult
import com.qubacy.geoqq.domain.common.operation.chat.ApproveNewMateRequestCreationOperation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

abstract class ChatUseCase<ChatStateType : ChatState> (
    errorDataRepository: ErrorDataRepository,
    val tokenDataRepository: TokenDataRepository,
    val imageDataRepository: ImageDataRepository,
    val userDataRepository: UserDataRepository,
    val mateRequestDataRepository: MateRequestDataRepository,
    otherFlowableRepositories: List<FlowableDataRepository>
) : ConsumingUseCase<ChatStateType>(
    errorDataRepository,
    listOf(userDataRepository) + otherFlowableRepositories
), UserExtension, ImageExtension, TokenExtension, MessageExtension {
    companion object {
        const val TAG = "ChatUseCase"
    }

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

            else -> {
                return false
            }
        }

        if (result is ErrorResult) processError(result.errorId)

        return true
    }

    protected open suspend fun processGetUsersByIdsResult(
        getUsersByIdsResult: GetUsersByIdsResult
    ): Result {
        val prevState = lockLastState()

        val getUsersFromGetUsersByIdsResult = getUsersFromGetUsersByIdsResult(
            getUsersByIdsResult, userDataRepository,
            imageDataRepository, tokenDataRepository, this
        )

        if (getUsersFromGetUsersByIdsResult is ErrorResult) return getUsersFromGetUsersByIdsResult

        val state = generateChatState(
            prevState?.messages ?: listOf(),
            (getUsersFromGetUsersByIdsResult as GetUsersFromGetUsersByIdsResult).users,
            listOf(
                SetUsersDetailsOperation(
                    getUsersByIdsResult.users.map { it.id }, !getUsersByIdsResult.areLocal
                )
            )
        )

        postState(state)

        return ProcessGetUserByIdResult()
    }

    protected abstract suspend fun processGetMessagesResult(
        getMessagesResult: GetMessagesResult
    ): Result

    protected abstract fun generateChatState(
        messages: List<Message>, users: List<User>, operations: List<Operation>
    ): ChatStateType

    open fun getUserDetails(userId: Long) {
        mCoroutineScope.launch(Dispatchers.IO) {
            // todo: is it ok??? we don't set the token rep. as a current one;
            mCurrentRepository = userDataRepository
            val getUsersResult = getUsers(
                listOf(userId), tokenDataRepository, this@ChatUseCase,
                userDataRepository, imageDataRepository, this@ChatUseCase,
                false, false
            )

            if (getUsersResult is ErrorResult) return@launch processError(getUsersResult.errorId)
            if (getUsersResult is InterruptionResult) return@launch processInterruption()

            val getUsersResultCast = getUsersResult as GetUsersResult

            val prevState = lockLastState()
            val updatedInterlocutorUser =
                getUsersResultCast.users.find { user -> user.id == userId }!!  // todo: delete this!!!

            var isInterlocutorUserUpdated = false
            val updatedUsers = prevState?.users?.map { prevUser ->
                if (prevUser.id == userId) {
                    if (prevUser != updatedInterlocutorUser) isInterlocutorUserUpdated = true

                    updatedInterlocutorUser
                    // todo: change to: getUsersResultCast.users.first()
                } else prevUser
            }

            val state = generateChatState(
                prevState?.messages ?: listOf(),
                updatedUsers ?: listOf(),
                listOf(
                    SetUsersDetailsOperation(
                        listOf(userId),
                        isInterlocutorUserUpdated
                    )
                )
            )

            postState(state)
        }
    }

    open fun createMateRequest(userId: Long) {
        mCoroutineScope.launch(Dispatchers.IO) {
            mCurrentRepository = tokenDataRepository
            val getAccessTokenResult = getAccessToken(tokenDataRepository)

            if (getAccessTokenResult is ErrorResult)
                return@launch processError(getAccessTokenResult.errorId)
            if (getAccessTokenResult is InterruptionResult) return@launch processInterruption()

            val getAccessTokenResultCast = getAccessTokenResult as GetAccessTokenResult

            mCurrentRepository = mateRequestDataRepository
            val createMateRequestResult = mateRequestDataRepository.createMateRequest(
                getAccessTokenResultCast.accessToken, userId
            )

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
