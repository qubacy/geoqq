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
import com.qubacy.geoqq.data.user.repository.UserDataRepository
import com.qubacy.geoqq.data.user.repository.result.GetUsersByIdsResult
import com.qubacy.geoqq.domain.common.model.user.User
import com.qubacy.geoqq.domain.common.model.message.common.MessageBase
import com.qubacy.geoqq.domain.common.result.common.ProcessGetUserByIdResult
import com.qubacy.geoqq.domain.common.usecase.consuming.ConsumingUseCase
import com.qubacy.geoqq.domain.common.usecase.util.extension.image.ImageExtension
import com.qubacy.geoqq.domain.common.usecase.util.extension.token.TokenExtension
import com.qubacy.geoqq.domain.common.usecase.util.extension.token.result.GetAccessTokenResult
import com.qubacy.geoqq.domain.common.usecase.util.extension.user.UserExtension
import com.qubacy.geoqq.domain.common.operation.chat.SetUsersDetailsOperation
import com.qubacy.geoqq.domain.common.usecase.util.extension.user.result.GetUsersFromGetUsersByIdsResult
import com.qubacy.geoqq.domain.common.usecase.util.extension.user.result.GetUsersResult
import com.qubacy.geoqq.domain.mate.chats.model.MateChat
import com.qubacy.geoqq.domain.mate.chats.operation.AddPrecedingChatsOperation
import com.qubacy.geoqq.domain.mate.chats.operation.SetMateChatsOperation
import com.qubacy.geoqq.domain.mate.chats.result.ProcessDataMateChatResult
import com.qubacy.geoqq.domain.mate.chats.result.ProcessDataMateChatsResult
import com.qubacy.geoqq.domain.mate.chats.result.ProcessGetChatsResult
import com.qubacy.geoqq.domain.mate.chats.state.MateChatsState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

open class MateChatsUseCase(
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

    private var mLastPrecedingChats: List<MateChat>? = null

    private var mMateChatsGotten = false

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

        val getUsersFromGetUsersByIdsResultCast =
            getUsersFromGetUsersByIdsResult as GetUsersFromGetUsersByIdsResult

        val users = retrieveUsers(prevState, false, getUsersFromGetUsersByIdsResultCast.users)

        val state = MateChatsState(
            prevState!!.chats,
            users,
            prevState.mateRequestCount,
            listOf(SetUsersDetailsOperation(getUsersByIdsResult.users.map { it.id }, true))
        )

        postState(state)

        return ProcessGetUserByIdResult()
    }

    private suspend fun processGetChatsResult(result: GetChatsResult): Result {
        val prevState = lockLastState()

        val getAccessTokenResult = getAccessToken(tokenDataRepository)

        if (getAccessTokenResult is ErrorResult) return getAccessTokenResult

        val getAccessTokenResultCast = getAccessTokenResult as GetAccessTokenResult

        val usersIds = result.chats.map { it.userId }
        val initUsers = if (!mMateChatsGotten) {
            val getUsersResult = getUsers(
                usersIds, getAccessTokenResultCast.accessToken,
                userDataRepository, imageDataRepository, this
            )

            if (getUsersResult is ErrorResult) return getUsersResult

            (getUsersResult as GetUsersResult).users
        } else
            prevState!!.users

        val processDataMateChatsResult = processDataMateChats(
            result.chats, initUsers//getUsersResultCast.users
        )

        if (processDataMateChatsResult is ErrorResult) return processDataMateChatsResult

        val processDataMateChatsResultCast = processDataMateChatsResult as ProcessDataMateChatsResult

        val chats = retrieveChats(prevState, result, processDataMateChatsResultCast)
        val users = retrieveUsers(prevState, result.isInitial, initUsers)

        val mateRequestCount = if (!mMateChatsGotten) {
            val getMateRequestCountResult =
                getMateRequestCount(getAccessTokenResultCast.accessToken) // todo: is this legal?

            if (getMateRequestCountResult is ErrorResult) return getMateRequestCountResult

            (getMateRequestCountResult as GetMateRequestCountResult).mateRequestCount
        } else
            prevState!!.mateRequestCount

        val operations =  if (result.isInitial) listOf(SetMateChatsOperation()) else
            listOf(
                AddPrecedingChatsOperation(processDataMateChatsResultCast.mateChats, !result.isLocal)
            )

        if (!mMateChatsGotten) mMateChatsGotten = true

        val newState = MateChatsState(
            chats,
            users,
            mateRequestCount,
            operations
        )

        postState(newState)

        return ProcessGetChatsResult()
    }

    private fun retrieveUsers(
        prevState: MateChatsState?,
        isInitial: Boolean,
        initUsers: List<User>
    ): List<User> {
        return if (isInitial) {
            initUsers
        } else {
            (prevState!!.users + initUsers).toSet().toList()
        }
    }

    private fun retrieveChats(
        prevState: MateChatsState?,
        getChatsResult: GetChatsResult,
        processDataMateChatsResult: ProcessDataMateChatsResult
    ): List<MateChat> {
        return if (getChatsResult.isInitial) {
            processDataMateChatsResult.mateChats
        } else {
            prevState!!

            if (getChatsResult.isLocal) {
                val newChats = prevState.chats + processDataMateChatsResult.mateChats

                mLastPrecedingChats = processDataMateChatsResult.mateChats
                newChats
            } else {
                val newChats = if (mLastPrecedingChats == null)
                    prevState.chats + processDataMateChatsResult.mateChats
                else {
                    val curChatsCount = prevState.chats.size

                    prevState.chats
                        .subList(0, curChatsCount - mLastPrecedingChats!!.size
                        ) + processDataMateChatsResult.mateChats
                }

                mLastPrecedingChats = null
                newChats
            }
        }
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
        val lastMessage = if (dataMateChat.lastMessage != null)
            MessageBase(dataMateChat.lastMessage.text, dataMateChat.lastMessage.time)
        else
            null

        val mateChat = MateChat(
            dataMateChat.id, dataMateChat.userId,
            user.avatarUri, lastMessage, dataMateChat.newMessageCount
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

    open fun getMateChats(count: Int) {
        mMateChatsGotten = false

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