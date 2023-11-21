package com.qubacy.geoqq.domain.mate.request

import android.util.Log
import com.qubacy.geoqq.data.common.repository.common.result.common.Result
import com.qubacy.geoqq.data.common.repository.common.result.error.ErrorResult
import com.qubacy.geoqq.data.common.repository.common.result.interruption.InterruptionResult
import com.qubacy.geoqq.data.error.repository.ErrorDataRepository
import com.qubacy.geoqq.data.image.repository.ImageDataRepository
import com.qubacy.geoqq.data.mate.request.model.DataMateRequest
import com.qubacy.geoqq.data.mate.request.repository.MateRequestDataRepository
import com.qubacy.geoqq.data.mate.request.repository.result.GetMateRequestsResult
import com.qubacy.geoqq.data.token.repository.TokenDataRepository
import com.qubacy.geoqq.data.user.repository.UserDataRepository
import com.qubacy.geoqq.data.user.repository.result.GetUsersByIdsResult
import com.qubacy.geoqq.domain.common.model.User
import com.qubacy.geoqq.domain.common.operation.chat.SetUsersDetailsOperation
import com.qubacy.geoqq.domain.common.operation.common.Operation
import com.qubacy.geoqq.domain.common.result.ProcessGetUserByIdResult
import com.qubacy.geoqq.domain.common.usecase.consuming.ConsumingUseCase
import com.qubacy.geoqq.domain.common.usecase.util.extension.image.ImageExtension
import com.qubacy.geoqq.domain.common.usecase.util.extension.token.TokenExtension
import com.qubacy.geoqq.domain.common.usecase.util.extension.token.result.GetAccessTokenResult
import com.qubacy.geoqq.domain.common.usecase.util.extension.user.UserExtension
import com.qubacy.geoqq.domain.common.usecase.util.extension.user.result.GetUsersFromGetUsersByIdsResult
import com.qubacy.geoqq.domain.common.usecase.util.extension.user.result.GetUsersResult
import com.qubacy.geoqq.domain.mate.request.model.MateRequest
import com.qubacy.geoqq.domain.mate.request.operation.MateRequestAnswerProcessedOperation
import com.qubacy.geoqq.domain.mate.request.operation.SetMateRequestsOperation
import com.qubacy.geoqq.domain.mate.request.result.ProcessDataMateRequestsResult
import com.qubacy.geoqq.domain.mate.request.result.ProcessMateRequestsResult
import com.qubacy.geoqq.domain.mate.request.state.MateRequestsState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MateRequestsUseCase(
    errorDataRepository: ErrorDataRepository,
    val tokenDataRepository: TokenDataRepository,
    val requestDataRepository: MateRequestDataRepository,
    val userDataRepository: UserDataRepository,
    val imageDataRepository: ImageDataRepository
) : ConsumingUseCase<MateRequestsState>(
    errorDataRepository,
    listOf(requestDataRepository, userDataRepository)
), TokenExtension, UserExtension, ImageExtension {
    companion object {
        const val TAG = "MateRequestsUseCase"
    }

    override suspend fun processResult(result: Result): Boolean {
        if (super.processResult(result)) return true

        val result = when (result::class) {
            GetMateRequestsResult::class -> {
                val getMateRequestsResult = result as GetMateRequestsResult

                processMateRequestsResult(getMateRequestsResult)
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

    private suspend fun processMateRequestsResult(
        getMateRequestsResult: GetMateRequestsResult
    ): Result {
        val prevState = lockLastState()

        val usersIdsToGet = getMateRequestsResult.mateRequests.map { it.userId }
        val getUsersResult = getUsers(
            usersIdsToGet, tokenDataRepository,
            this, userDataRepository,
            imageDataRepository, this
        )

        if (getUsersResult is ErrorResult) return getUsersResult

        val getUsersResultCast = getUsersResult as GetUsersResult

        val processDataMateRequestsResult = processDataMateRequests(
            getMateRequestsResult.mateRequests, getUsersResultCast.users)

        if (processDataMateRequestsResult is ErrorResult) return processDataMateRequestsResult

        val state = MateRequestsState(
            (processDataMateRequestsResult as ProcessDataMateRequestsResult).mateRequests,
            getUsersResultCast.users,
            listOf(SetMateRequestsOperation())
        )

        postState(state)

        return ProcessMateRequestsResult()
    }

    private fun processDataMateRequests(
        dataMateRequests: List<DataMateRequest>,
        users: List<User> // todo: will i really need it??;
    ): Result {
        val mateRequests = dataMateRequests.map { MateRequest(it.id, it.userId) }

        return ProcessDataMateRequestsResult(mateRequests)
    }

    private suspend fun processGetUsersByIdsResult(getUsersByIdsResult: GetUsersByIdsResult): Result {
        val prevState = lockLastState()

        val getUsersFromGetUsersByIdsResult = getUsersFromGetUsersByIdsResult(
            getUsersByIdsResult, userDataRepository,
            imageDataRepository, tokenDataRepository, this)

        if (getUsersFromGetUsersByIdsResult is ErrorResult) return getUsersFromGetUsersByIdsResult

        val state = MateRequestsState(
            prevState?.mateRequests ?: listOf(),
            (getUsersFromGetUsersByIdsResult as GetUsersFromGetUsersByIdsResult).users,
            listOf(SetUsersDetailsOperation(getUsersByIdsResult.users.map { it.id }, true))
        )

        postState(state)

        return ProcessGetUserByIdResult()
    }

    override fun generateState(
        operations: List<Operation>,
        prevState: MateRequestsState?
    ): MateRequestsState {
        return MateRequestsState(
            prevState?.mateRequests ?: listOf(),
            prevState?.users ?: listOf(),
            operations
        )
    }

    fun getMateRequests(count: Int) {
        mCoroutineScope.launch(Dispatchers.IO) {
            mCurrentRepository = tokenDataRepository
            val getAccessTokenResult = getAccessToken(tokenDataRepository)

            if (getAccessTokenResult is ErrorResult)
                return@launch processError(getAccessTokenResult.errorId)
            if (getAccessTokenResult is InterruptionResult) return@launch processInterruption()

            val getAccessTokenResultCast = getAccessTokenResult as GetAccessTokenResult

            requestDataRepository.getMateRequests(getAccessTokenResultCast.accessToken, count)
        }
    }

    fun answerMateRequest(mateRequestId: Long, isAccepted: Boolean) {
        mCoroutineScope.launch(Dispatchers.IO) {
            mCurrentRepository = tokenDataRepository
            val getAccessTokenResult = getAccessToken(tokenDataRepository)

            if (getAccessTokenResult is ErrorResult)
                return@launch processError(getAccessTokenResult.errorId)
            if (getAccessTokenResult is InterruptionResult) return@launch processInterruption()

            val getAccessTokenResultCast = getAccessTokenResult as GetAccessTokenResult

            val answerMateRequestResult = requestDataRepository.answerMateRequest(
                getAccessTokenResultCast.accessToken, mateRequestId, isAccepted)

            if (answerMateRequestResult is ErrorResult)
                return@launch processError(answerMateRequestResult.errorId)
            if (answerMateRequestResult is InterruptionResult) return@launch processInterruption()

            val prevState = lockLastState()
            val newState = MateRequestsState(
                prevState?.mateRequests?.filter { it.id != mateRequestId } ?: listOf(),
                prevState?.users ?: listOf(),
                listOf(MateRequestAnswerProcessedOperation())
            )

            postState(newState)
        }
    }
}