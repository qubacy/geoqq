package com.qubacy.geoqq.domain.mate.chats

import com.qubacy.geoqq.domain.common.operation.common.Operation
import com.qubacy.geoqq.data.common.repository.common.result.common.Result
import com.qubacy.geoqq.data.common.repository.common.result.error.ErrorResult
import com.qubacy.geoqq.data.common.repository.common.result.interruption.InterruptionResult
import com.qubacy.geoqq.data.error.repository.ErrorDataRepository
import com.qubacy.geoqq.data.image.repository.ImageDataRepository
import com.qubacy.geoqq.data.mate.chat.model.DataMateChat
import com.qubacy.geoqq.data.mate.chat.repository.MateChatDataRepository
import com.qubacy.geoqq.data.mate.chat.repository.result.GetChatsResult
import com.qubacy.geoqq.data.mate.request.repository.MateRequestDataRepository
import com.qubacy.geoqq.data.mate.request.repository.result.GetMateRequestCountResult
import com.qubacy.geoqq.data.token.repository.TokenDataRepository
import com.qubacy.geoqq.data.token.repository.result.GetTokensResult
import com.qubacy.geoqq.data.user.repository.UserDataRepository
import com.qubacy.geoqq.data.user.repository.result.GetUserByIdResult
import com.qubacy.geoqq.domain.common.model.User
import com.qubacy.geoqq.domain.common.model.message.common.MessageBase
import com.qubacy.geoqq.domain.common.result.ProcessGetUserByIdResult
import com.qubacy.geoqq.domain.common.usecase.consuming.ConsumingUseCase
import com.qubacy.geoqq.domain.common.usecase.util.extension.image.ImageExtension
import com.qubacy.geoqq.domain.common.usecase.util.extension.image.result.GetImageUriResult
import com.qubacy.geoqq.domain.common.usecase.util.extension.token.TokenExtension
import com.qubacy.geoqq.domain.common.usecase.util.extension.token.result.GetAccessTokenResult
import com.qubacy.geoqq.domain.common.usecase.util.extension.user.UserExtension
import com.qubacy.geoqq.domain.common.operation.chat.SetUserDetailsOperation
import com.qubacy.geoqq.domain.common.usecase.util.extension.user.result.GetUserResult
import com.qubacy.geoqq.domain.mate.chats.model.MateChat
import com.qubacy.geoqq.domain.mate.chats.operation.SetMateChatsOperation
import com.qubacy.geoqq.domain.mate.chats.result.ProcessDataMateChatResult
import com.qubacy.geoqq.domain.mate.chats.result.ProcessGetChatsResult
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
), UserExtension, ImageExtension, TokenExtension {
    override suspend fun processResult(result: Result): Boolean {
        if (super.processResult(result)) return true

        val result = when (result::class) {
            GetChatsResult::class -> {
                val getChatsResult = result as GetChatsResult

                processGetChatsResult(getChatsResult)
            }
            GetUserByIdResult::class -> {
                val getUserByIdResult = result as GetUserByIdResult

                processGetUserByIdResult(getUserByIdResult)
            }
            else -> { return false }
        }

        if (result is ErrorResult) processError(result.errorId)

        return true
    }

    private suspend fun processGetUserByIdResult(getUserByIdResult: GetUserByIdResult): Result {
        val getAccessTokenResult = getAccessToken(tokenDataRepository)

        if (getAccessTokenResult is ErrorResult) return getAccessTokenResult

        val getAccessTokenResultCast = getAccessTokenResult as GetAccessTokenResult

        val getImageUriResult = getImageUri(
            getUserByIdResult.user.avatarId,
            getAccessTokenResultCast.accessToken,
            imageDataRepository
        )

        if (getImageUriResult is ErrorResult) return getImageUriResult

        val getImageUriResultCast = getImageUriResult as GetImageUriResult

        val updatedUser = User(
            getUserByIdResult.user.id,
            getUserByIdResult.user.username,
            getUserByIdResult.user.description,
            getImageUriResultCast.imageUri,
            getUserByIdResult.user.isMate
        )

        val prevState = lockLastState()

        val updatedUsers = prevState?.users?.map {
            if (it.id == getUserByIdResult.user.id) updatedUser
            else it
        }!!
        val state = MateChatsState(
            prevState.chats,
            updatedUsers,
            prevState.mateRequestCount,
            listOf(SetUserDetailsOperation(getUserByIdResult.user.id))
        )

        postState(state)

        return ProcessGetUserByIdResult()
    }

    private suspend fun processGetChatsResult(result: GetChatsResult): Result {
        lockLastState()

        val getAccessTokenResult = getAccessToken(tokenDataRepository)

        if (getAccessTokenResult is ErrorResult) return getAccessTokenResult

        val getAccessTokenResultCast = getAccessTokenResult as GetAccessTokenResult

        val mateChats = mutableListOf<MateChat>()
        val mateUsers = mutableListOf<User>()

        for (dataChat in result.chats) {
            val processDataMateChatResult = processDataMateChat(
                dataChat, getAccessTokenResultCast.accessToken)

            if (processDataMateChatResult is ErrorResult) return processDataMateChatResult

            val processDataMateChatResultCast = processDataMateChatResult as ProcessDataMateChatResult

            mateChats.add(processDataMateChatResultCast.mateChat)
            mateUsers.add(processDataMateChatResultCast.mateUser)
        }

        val getMateRequestCountResult = getMateRequestCount(getAccessTokenResultCast.accessToken) // todo: is this legal?

        if (getMateRequestCountResult is ErrorResult) return getMateRequestCountResult

        val getMateRequestCountResultCast = getMateRequestCountResult as GetMateRequestCountResult

        val newState = MateChatsState(
            mateChats,
            mateUsers,
            getMateRequestCountResultCast.mateRequestCount,
            listOf(SetMateChatsOperation())
        )

        postState(newState)

        return ProcessGetChatsResult()
    }

    private suspend fun getMateRequestCount(accessToken: String): Result {
        val getRequestCountResult = mateRequestDataRepository.getMateRequestCount(accessToken)

        if (getRequestCountResult is ErrorResult) return getRequestCountResult

        val getRequestCountResultCast = getRequestCountResult as GetMateRequestCountResult

        return GetMateRequestCountResult(getRequestCountResultCast.mateRequestCount)
    }

    private suspend fun processDataMateChat(dataMateChat: DataMateChat, accessToken: String): Result {
        val getUserResult = getUser(
            dataMateChat.userId, accessToken,
            userDataRepository, imageDataRepository, this
        )

        if (getUserResult is ErrorResult) return getUserResult

        val getUserResultCast = getUserResult as GetUserResult

        val lastMessage = if (dataMateChat.lastMessage != null)
            MessageBase(dataMateChat.lastMessage.text, dataMateChat.lastMessage.time)
        else
            null

        val mateChat = MateChat(
            dataMateChat.id, dataMateChat.userId, getUserResultCast.user.avatarUri, lastMessage
        )

        return ProcessDataMateChatResult(mateChat, getUserResultCast.user)
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