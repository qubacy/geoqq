package com.qubacy.geoqq.domain.mate.chats

import android.util.Log
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
import com.qubacy.geoqq.data.user.model.DataUser
import com.qubacy.geoqq.data.user.repository.UserDataRepository
import com.qubacy.geoqq.data.user.repository.result.GetUsersByIdsResult
import com.qubacy.geoqq.domain.common.model.User
import com.qubacy.geoqq.domain.common.model.message.common.MessageBase
import com.qubacy.geoqq.domain.common.result.ProcessGetUserByIdResult
import com.qubacy.geoqq.domain.common.usecase.consuming.ConsumingUseCase
import com.qubacy.geoqq.domain.common.usecase.util.extension.image.ImageExtension
import com.qubacy.geoqq.domain.common.usecase.util.extension.token.TokenExtension
import com.qubacy.geoqq.domain.common.usecase.util.extension.token.result.GetAccessTokenResult
import com.qubacy.geoqq.domain.common.usecase.util.extension.user.UserExtension
import com.qubacy.geoqq.domain.common.operation.chat.SetUsersDetailsOperation
import com.qubacy.geoqq.domain.common.usecase.util.extension.user.result.GetUsersAvatarUrisResult
import com.qubacy.geoqq.domain.common.usecase.util.extension.user.result.GetUsersFromGetUsersByIdsResult
import com.qubacy.geoqq.domain.common.usecase.util.extension.user.result.GetUsersResult
import com.qubacy.geoqq.domain.common.usecase.util.extension.user.result.ProcessDataUsersResult
import com.qubacy.geoqq.domain.mate.chats.model.MateChat
import com.qubacy.geoqq.domain.mate.chats.operation.SetMateChatsOperation
import com.qubacy.geoqq.domain.mate.chats.result.ProcessDataMateChatResult
import com.qubacy.geoqq.domain.mate.chats.result.ProcessDataMateChatsResult
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
    companion object {
        const val TAG = "MateChatsUseCase"
    }

    override suspend fun processResult(result: Result): Boolean {
        if (super.processResult(result)) return true

        val result = when (result::class) {
            GetChatsResult::class -> {
                val getChatsResult = result as GetChatsResult

                processGetChatsResult(getChatsResult)
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

    private suspend fun processGetUsersByIdsResult(getUsersByIdsResult: GetUsersByIdsResult): Result {
        val prevState = lockLastState()

        val getUsersFromGetUsersByIdsResult = getUsersFromGetUsersByIdsResult(
            getUsersByIdsResult, userDataRepository,
            imageDataRepository, tokenDataRepository, this)

        if (getUsersFromGetUsersByIdsResult is ErrorResult) return getUsersFromGetUsersByIdsResult

        val state = MateChatsState(
            prevState!!.chats,
            (getUsersFromGetUsersByIdsResult as GetUsersFromGetUsersByIdsResult).users,
            prevState.mateRequestCount,
            listOf(SetUsersDetailsOperation(getUsersByIdsResult.users.map { it.id }, true))
        )

        Log.d(TAG, "processGetUserByIdResult(): posting a state with areLocal = ${getUsersByIdsResult.areLocal}")
        postState(state)

        return ProcessGetUserByIdResult()
    }

    private suspend fun processGetChatsResult(result: GetChatsResult): Result {
        Log.d(TAG, "processGetChatsResult(): before posting a state with result.isLocal = ${result.isLocal})")

        lockLastState()

        val getAccessTokenResult = getAccessToken(tokenDataRepository)

        if (getAccessTokenResult is ErrorResult) return getAccessTokenResult

        val getAccessTokenResultCast = getAccessTokenResult as GetAccessTokenResult

        val usersIds = result.chats.map { it.userId }
        val getUsersResult = getUsers(
            usersIds, getAccessTokenResultCast.accessToken,
            userDataRepository, imageDataRepository, this
        )

        val getUsersResultCast = getUsersResult as GetUsersResult

        val processDataMateChatsResult = processDataMateChats(
            result.chats, getUsersResultCast.users
        )

        if (processDataMateChatsResult is ErrorResult) return processDataMateChatsResult

        val getMateRequestCountResult = getMateRequestCount(getAccessTokenResultCast.accessToken) // todo: is this legal?

        if (getMateRequestCountResult is ErrorResult) return getMateRequestCountResult

        val getMateRequestCountResultCast = getMateRequestCountResult as GetMateRequestCountResult

        val newState = MateChatsState(
            (processDataMateChatsResult as ProcessDataMateChatsResult).mateChats,
            getUsersResultCast.users,
            getMateRequestCountResultCast.mateRequestCount,
            listOf(SetMateChatsOperation())
        )

        Log.d(TAG, "processGetChatsResult(): posting a state..")
        postState(newState)

        return ProcessGetChatsResult()
    }

    private fun processDataMateChats(
        dataMateChats: List<DataMateChat>, users: List<User>
    ): Result {
        val mateChats = mutableListOf<MateChat>()

        for (dataChat in dataMateChats) {
            val user = users.find { it.id == dataChat.userId }!!
            val processDataMateChatResult = processDataMateChat(dataChat, user)

            if (processDataMateChatResult is ErrorResult) return processDataMateChatResult

            val processDataMateChatResultCast = processDataMateChatResult as ProcessDataMateChatResult

            mateChats.add(processDataMateChatResultCast.mateChat)
        }

        return ProcessDataMateChatsResult(mateChats)
    }

    private suspend fun getMateRequestCount(accessToken: String): Result {
        val getRequestCountResult = mateRequestDataRepository.getMateRequestCount(accessToken)

        if (getRequestCountResult is ErrorResult) return getRequestCountResult

        val getRequestCountResultCast = getRequestCountResult as GetMateRequestCountResult

        return GetMateRequestCountResult(getRequestCountResultCast.mateRequestCount)
    }

    private fun processDataMateChat(
        dataMateChat: DataMateChat, user: User
    ): Result {
        Log.d(TAG, "processDataMateChat(): dataMateChat.id = ${dataMateChat.id}; dataMateChat.userId = ${dataMateChat.userId}")

        val lastMessage = if (dataMateChat.lastMessage != null)
            MessageBase(dataMateChat.lastMessage.text, dataMateChat.lastMessage.time)
        else
            null

        val mateChat = MateChat(
            dataMateChat.id, dataMateChat.userId, user.avatarUri, lastMessage
        )

        return ProcessDataMateChatResult(mateChat, user)
    }

    override fun generateState(
        operations: List<Operation>,
        prevState: MateChatsState?
    ): MateChatsState {
        return MateChatsState(
            prevState?.chats ?: listOf(),
            prevState?.users ?: listOf(),
            prevState?.mateRequestCount ?: 0,
            operations
        )
    }

    fun getMateChats(count: Int) {
        mCoroutineScope.launch(Dispatchers.IO) {
            mCurrentRepository = tokenDataRepository
            val getAccessTokenResult = getAccessToken(tokenDataRepository)

            if (getAccessTokenResult is ErrorResult)
                return@launch processError(getAccessTokenResult.errorId)
            if (getAccessTokenResult is InterruptionResult) return@launch processInterruption()

            val getAccessTokenResultCast = getAccessTokenResult as GetAccessTokenResult

            mCurrentRepository = mateChatDataRepository
            mateChatDataRepository.getChats(getAccessTokenResultCast.accessToken, count)
        }
    }
}