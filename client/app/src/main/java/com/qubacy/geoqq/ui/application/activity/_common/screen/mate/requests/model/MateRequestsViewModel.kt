package com.qubacy.geoqq.ui.application.activity._common.screen.mate.requests.model

import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.qubacy.geoqq.data.error.repository.ErrorDataRepository
import com.qubacy.geoqq.domain.interlocutor.usecase.result.interlocutor._common.InterlocutorDomainResult
import com.qubacy.geoqq.domain.mate.request.usecase.result.AnswerMateRequestDomainResult
import com.qubacy.geoqq.domain.mate.requests.usecase.MateRequestsUseCase
import com.qubacy.geoqq.domain.mate.requests.usecase.result.GetRequestChunkDomainResult
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.authorized.model.AuthorizedViewModel
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.authorized.model.result.handler.AuthorizedDomainResultHandler
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.interlocutor.model.InterlocutorViewModel
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.business.model.BusinessViewModel
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.interlocutor.model.result.handler.InterlocutorDomainResultHandler
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.business.model.result.handler._common.DomainResultHandler
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.stateful.model.operation._common.UiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.presentation.user.UserPresentation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.presentation.user.toUserPresentation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.requests._common.presentation.toMateRequestPresentation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.requests.model.operation.InsertRequestsUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.requests.model.operation.RemoveRequestUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.requests.model.operation.ReturnAnsweredRequestUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.requests.model.result.handler.MateRequestsDomainResultHandler
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.requests.model.state.MateRequestsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Qualifier

@HiltViewModel
open class MateRequestsViewModel @Inject constructor(
    mSavedStateHandle: SavedStateHandle,
    mErrorDataRepository: ErrorDataRepository,
    mMateRequestsUseCase: MateRequestsUseCase
) : BusinessViewModel<MateRequestsUiState, MateRequestsUseCase>(
    mSavedStateHandle, mErrorDataRepository, mMateRequestsUseCase
), AuthorizedViewModel, InterlocutorViewModel {
    private var mIsGettingNextRequestChunk = false

    override fun generateDomainResultHandlers(): Array<DomainResultHandler<*>> {
        return super.generateDomainResultHandlers()
            .plus(AuthorizedDomainResultHandler(this))
            .plus(InterlocutorDomainResultHandler(this))
            .plus(MateRequestsDomainResultHandler(this))
    }

    override fun generateDefaultUiState(): MateRequestsUiState {
        return MateRequestsUiState()
    }

    open fun getUserProfileWithMateRequestId(id: Long): UserPresentation {
        val user = mUiState.requests.find { it.id == id }!!.user // todo: is it alright?

        mUseCase.getInterlocutor(user.id) // todo: is it necessary?

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
        val prevRequestCount = mUiState.requests.size + mUiState.answeredRequestCount
        val preparedPrevRequestCount =
            if (prevRequestCount > 0) prevRequestCount - mUiState.newRequestCount
            else prevRequestCount

        val chunkSizeCheck =
            (preparedPrevRequestCount % MateRequestsUseCase.DEFAULT_REQUEST_CHUNK_SIZE == 0)

        return (!mIsGettingNextRequestChunk && chunkSizeCheck)
    }

    open fun resetRequests() {
        mUiState.apply {
            requests.clear()

            newRequestCount = 0
            answeredRequestCount = 0
        }
    }

    fun onMateRequestsGetRequestChunk(
        getRequestChunkResult: GetRequestChunkDomainResult
    ): List<UiOperation> {
        if (mUiState.isLoading) changeLoadingState(false)

        mIsGettingNextRequestChunk = false

        if (!getRequestChunkResult.isSuccessful())
            return onError(getRequestChunkResult.error!!)

        val requestChunk = getRequestChunkResult.chunk!!
        val requestChunkPosition = mUiState.requests.size
        val requests = requestChunk.requests.map { it.toMateRequestPresentation() }

        mUiState.requests.addAll(requests)

        return listOf(InsertRequestsUiOperation(requestChunkPosition, requests))
    }

    fun onMateRequestsAnswerMateRequest(
        answerRequestResult: AnswerMateRequestDomainResult
    ): List<UiOperation> {
        if (mUiState.isLoading) changeLoadingState(false)

        val requestId = answerRequestResult.requestId
        val requestPosition = mUiState.requests.indexOfFirst { it.id == requestId }

        if (!answerRequestResult.isSuccessful()) {
            onMateRequestAnsweringFailed(requestPosition)

            return onError(answerRequestResult.error!!)
        }

        mUiState.requests.removeAt(requestPosition)
        mUiState.answeredRequestCount++

        return listOf(RemoveRequestUiOperation(requestPosition))
    }

    override fun onInterlocutorInterlocutor(domainResult: InterlocutorDomainResult): UserPresentation {
        val userPresentation = domainResult.interlocutor!!.toUserPresentation()

        val requestPosition = mUiState.requests.indexOfFirst { it.user.id == userPresentation.id }

        mUiState.requests[requestPosition] =
            mUiState.requests[requestPosition].copy(user = userPresentation)

        return userPresentation
    }

    private fun onMateRequestAnsweringFailed(requestPosition: Int) {
        viewModelScope.launch {
            mUiOperationFlow.emit(ReturnAnsweredRequestUiOperation(requestPosition))
        }
    }

    override fun getInterlocutorViewModelBusinessViewModel(): BusinessViewModel<*, *> {
        return this
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