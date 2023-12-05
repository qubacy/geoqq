package com.qubacy.geoqq.domain.geochat.chat

import com.qubacy.geoqq.data.common.repository.message.result.GetMessagesResult
import com.qubacy.geoqq.data.common.repository.common.result.common.Result
import com.qubacy.geoqq.data.common.repository.common.result.error.ErrorResult
import com.qubacy.geoqq.data.common.repository.common.result.interruption.InterruptionResult
import com.qubacy.geoqq.data.error.repository.ErrorDataRepository
import com.qubacy.geoqq.data.geochat.message.repository.GeoMessageDataRepository
import com.qubacy.geoqq.data.image.repository.ImageDataRepository
import com.qubacy.geoqq.data.mate.request.repository.MateRequestDataRepository
import com.qubacy.geoqq.data.token.repository.TokenDataRepository
import com.qubacy.geoqq.data.user.repository.UserDataRepository
import com.qubacy.geoqq.domain.common.model.user.User
import com.qubacy.geoqq.domain.common.model.message.Message
import com.qubacy.geoqq.domain.common.operation.common.Operation
import com.qubacy.geoqq.domain.common.usecase.chat.ChatUseCase
import com.qubacy.geoqq.domain.common.usecase.util.extension.image.ImageExtension
import com.qubacy.geoqq.domain.common.usecase.util.extension.message.MessageExtension
import com.qubacy.geoqq.domain.common.usecase.util.extension.message.result.ProcessDataMessagesResult
import com.qubacy.geoqq.domain.common.usecase.util.extension.token.TokenExtension
import com.qubacy.geoqq.domain.common.usecase.util.extension.token.result.GetAccessTokenResult
import com.qubacy.geoqq.domain.common.usecase.util.extension.user.UserExtension
import com.qubacy.geoqq.domain.common.usecase.util.extension.user.result.GetLocalUserIdResult
import com.qubacy.geoqq.domain.common.usecase.util.extension.user.result.GetUsersResult
import com.qubacy.geoqq.domain.geochat.chat.state.GeoChatState
import com.qubacy.geoqq.domain.common.result.chat.ProcessGetMessagesResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

open class GeoChatUseCase(
    errorDataRepository: ErrorDataRepository,
    tokenDataRepository: TokenDataRepository,
    val geoMessageDataRepository: GeoMessageDataRepository,
    imageDataRepository: ImageDataRepository,
    userDataRepository: UserDataRepository,
    mateRequestDataRepository: MateRequestDataRepository
) : ChatUseCase<GeoChatState>(
    errorDataRepository,
    tokenDataRepository,
    imageDataRepository,
    userDataRepository,
    mateRequestDataRepository,
    listOf(geoMessageDataRepository)
), UserExtension, ImageExtension, TokenExtension, MessageExtension {
    companion object {
        const val TAG = "GeoChatUseCase"
    }

    private var mLocalUserId: Long = 0
    val localUserId get() = mLocalUserId

    override suspend fun processGetMessagesResult(getMessagesResult: GetMessagesResult): Result {
        lockLastState()

        val getAccessTokenResult = getAccessToken(tokenDataRepository)

        if (getAccessTokenResult is ErrorResult) return getAccessTokenResult

        val getAccessTokenResultCast = getAccessTokenResult as GetAccessTokenResult

        val userIdsToGet = getMessagesResult.messages.map { it.userId }.toSet().toList()
        val getUsersResult = getUsers(
            userIdsToGet,
            getAccessTokenResultCast.accessToken,
            userDataRepository,
            imageDataRepository,
            this
        )

        if (getUsersResult is ErrorResult) return getUsersResult

        val getUsersResultCast = getUsersResult as GetUsersResult

        val processDataMessagesResult = processDataMessages(getMessagesResult.messages)

        if (processDataMessagesResult is ErrorResult) return processDataMessagesResult

        val processDataMessagesResultCast = processDataMessagesResult as ProcessDataMessagesResult

        val messages = processDataMessagesResultCast.messages
        val operations = listOf(com.qubacy.geoqq.domain.common.operation.chat.SetMessagesOperation())

        val state = GeoChatState(
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
    ): GeoChatState {
        return GeoChatState(messages, users, operations)
    }

    override fun generateState(
        operations: List<Operation>,
        prevState: GeoChatState?
    ): GeoChatState {
        return GeoChatState(
            prevState?.messages ?: listOf(),
            prevState?.users ?: listOf(),
            operations
        )
    }

    fun getGeoChat(
        radius: Int,
        latitude: Double,
        longitude: Double
    ) {
        mCoroutineScope.launch(Dispatchers.IO) {
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

            mCurrentRepository = geoMessageDataRepository
            geoMessageDataRepository.getGeoMessages(
                radius, latitude, longitude, getAccessTokenResultCast.accessToken)
        }
    }

    fun sendGeoMessage(
        radius: Int,
        latitude: Double,
        longitude: Double,
        text: String
    ) {
        mCoroutineScope.launch(Dispatchers.IO) {
            mCurrentRepository = tokenDataRepository
            val getAccessTokenResult = getAccessToken(tokenDataRepository)

            if (getAccessTokenResult is ErrorResult)
                return@launch processError(getAccessTokenResult.errorId)
            if (getAccessTokenResult is InterruptionResult) return@launch processInterruption()

            val getAccessTokenResultCast = getAccessTokenResult as GetAccessTokenResult

            mCurrentRepository = geoMessageDataRepository
            val sendGeoMessageResult = geoMessageDataRepository.sendGeoMessage(
                radius, latitude, longitude, text, getAccessTokenResultCast.accessToken)

            if (sendGeoMessageResult is ErrorResult)
                return@launch processError(sendGeoMessageResult.errorId)
            if (sendGeoMessageResult is InterruptionResult) return@launch processInterruption()
        }
    }
}