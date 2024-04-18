package com.qubacy.geoqq.ui.application.activity._common.screen.mate.requests.model

import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.qubacy.geoqq.data.error.repository.ErrorDataRepository
import com.qubacy.geoqq.domain._common.usecase._common.result._common.DomainResult
import com.qubacy.geoqq.domain.interlocutor.usecase.result.interlocutor.GetInterlocutorDomainResult
import com.qubacy.geoqq.domain.interlocutor.usecase.result.interlocutor.UpdateInterlocutorDomainResult
import com.qubacy.geoqq.domain.interlocutor.usecase.result.interlocutor._common.InterlocutorDomainResult
import com.qubacy.geoqq.domain.mate.request.usecase.result.AnswerMateRequestDomainResult
import com.qubacy.geoqq.domain.mate.requests.usecase.MateRequestsUseCase
import com.qubacy.geoqq.domain.mate.requests.usecase.result.GetRequestChunkDomainResult
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.business.model.BusinessViewModel
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.mateable.model.operation.ShowInterlocutorDetailsUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.mateable.model.operation.UpdateInterlocutorDetailsUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.stateful.model.operation._common.UiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.presentation.user.UserPresentation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.presentation.user.toUserPresentation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.requests._common.presentation.toMateRequestPresentation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.requests.model.operation.InsertRequestsUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.requests.model.operation.RemoveRequestUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.requests.model.state.MateRequestsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import javax.inject.Qualifier

@HiltViewModel
open class MateRequestsViewModel @Inject constructor(
    mSavedStateHandle: SavedStateHandle,
    mErrorDataRepository: ErrorDataRepository,
    mMateRequestsUseCase: MateRequestsUseCase
) : BusinessViewModel<MateRequestsUiState, MateRequestsUseCase>(
    mSavedStateHandle, mErrorDataRepository, mMateRequestsUseCase
) {
    private var mIsGettingNextRequestChunk = false

    override fun generateDefaultUiState(): MateRequestsUiState {
        return MateRequestsUiState()
    }

    open fun getUserProfileWithMateRequestId(id: Long): UserPresentation {
        val user = mUiState.requests.find { it.id == id }!!.user // todo: is it alright?

        mUseCase.getInterlocutor(user.id)

        return user
    }

    open fun getNextRequestChunk() {
        if (!isNextRequestChunkGettingAllowed()) return

        mIsGettingNextRequestChunk = true

        changeLoadingState(true)

        mUseCase.getRequestChunk(mUiState.requests.size)
    }

    open fun answerRequest(position: Int, isAccepted: Boolean) {
        changeLoadingState(true)

        val request = mUiState.requests[position]

        mUseCase.answerRequest(request.id, isAccepted)
    }

    open fun isNextRequestChunkGettingAllowed(): Boolean {
        val prevRequestCount = mUiState.requests.size -
                mUiState.newRequestCount + mUiState.answeredRequestCount

        val chunkSizeCheck = (mUiState.requests.isEmpty() ||
                (prevRequestCount % MateRequestsUseCase.DEFAULT_REQUEST_CHUNK_SIZE == 0))

        return (!mIsGettingNextRequestChunk && chunkSizeCheck)
    }

    open fun resetRequests() {
        mUiState.apply {
            requests.clear()
            newRequestCount = 0
            answeredRequestCount = 0
        }
    }

    override fun processDomainResultFlow(domainResult: DomainResult): UiOperation? {
        val uiOperation = super.processDomainResultFlow(domainResult)

        if (uiOperation != null) return uiOperation

        return when (domainResult::class) {
            GetRequestChunkDomainResult::class ->
                processGetRequestChunkDomainResult(domainResult as GetRequestChunkDomainResult)
            GetInterlocutorDomainResult::class ->
                processGetInterlocutorDomainResult(domainResult as GetInterlocutorDomainResult)
            UpdateInterlocutorDomainResult::class ->
                processUpdateInterlocutorDomainResult(domainResult as UpdateInterlocutorDomainResult)
            AnswerMateRequestDomainResult::class ->
                processAnswerMateRequestDomainResult(domainResult as AnswerMateRequestDomainResult)
            else -> null
        }
    }

    private fun processGetInterlocutorDomainResult(
        getInterlocutorResult: GetInterlocutorDomainResult
    ): UiOperation {
        if (!getInterlocutorResult.isSuccessful())
            return processErrorDomainResult(getInterlocutorResult.error!!)

        val userPresentation = processInterlocutorResult(getInterlocutorResult)

        return ShowInterlocutorDetailsUiOperation(userPresentation)
    }

    private fun processUpdateInterlocutorDomainResult(
        updateInterlocutorResult: UpdateInterlocutorDomainResult
    ): UiOperation {
        if (!updateInterlocutorResult.isSuccessful())
            return processErrorDomainResult(updateInterlocutorResult.error!!)

        val userPresentation = processInterlocutorResult(updateInterlocutorResult)

        return UpdateInterlocutorDetailsUiOperation(userPresentation)
    }

    private fun processInterlocutorResult(
        interlocutorResult: InterlocutorDomainResult
    ): UserPresentation {
        val userPresentation = interlocutorResult.interlocutor!!.toUserPresentation()

        val requestPosition = mUiState.requests.indexOfFirst {
            it.user.id == userPresentation.id }

        mUiState.requests[requestPosition] =
            mUiState.requests[requestPosition].copy(user = userPresentation)

        return userPresentation
    }

    private fun processGetRequestChunkDomainResult(
        getRequestChunkResult: GetRequestChunkDomainResult
    ): UiOperation {
        if (mUiState.isLoading) changeLoadingState(false)

        mIsGettingNextRequestChunk = false

        if (!getRequestChunkResult.isSuccessful())
            return processErrorDomainResult(getRequestChunkResult.error!!)

        val requestChunk = getRequestChunkResult.chunk!!
        val requestOffset = requestChunk.offset
        val requests = requestChunk.requests.map { it.toMateRequestPresentation() }

        mUiState.requests.addAll(requests)

        return InsertRequestsUiOperation(requestOffset, requests)
    }

    private fun processAnswerMateRequestDomainResult(
        answerRequestResult: AnswerMateRequestDomainResult
    ): UiOperation {
        if (mUiState.isLoading) changeLoadingState(false)

        if (!answerRequestResult.isSuccessful())
            return processErrorDomainResult(answerRequestResult.error!!)

        val requestId = answerRequestResult.requestId!!
        val requestPosition = mUiState.requests.indexOfFirst { it.id == requestId }

        mUiState.requests.removeAt(requestPosition)
        mUiState.answeredRequestCount++

        return RemoveRequestUiOperation(requestPosition)
    }

    private fun getRequestChunkPositionByChunkIndex(index: Int): Int {
        return index * MateRequestsUseCase.DEFAULT_REQUEST_CHUNK_SIZE
    }
}

@Qualifier
annotation class MateRequestsViewModelFactoryQualifier

class MateRequestsViewModelFactory(
    private val mErrorDataRepository: ErrorDataRepository,
    private val mMateRequestsUseCase: MateRequestsUseCase
) : AbstractSavedStateViewModelFactory() {
    override fun <T : ViewModel> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle
    ): T {
        if (!modelClass.isAssignableFrom(MateRequestsViewModel::class.java))
            throw IllegalArgumentException()

        return MateRequestsViewModel(handle, mErrorDataRepository, mMateRequestsUseCase) as T
    }
}