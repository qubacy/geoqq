package com.qubacy.geoqq.ui.application.activity._common.screen.mate.requests.model.impl

import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.qubacy.geoqq.data._common.repository._common.source.local.database.error._common.LocalErrorDatabaseDataSource
import com.qubacy.geoqq.domain.user.usecase._common.result.update.UpdateUserDomainResult
import com.qubacy.geoqq.domain.user.usecase._common.result._common.UserDomainResult
import com.qubacy.geoqq.domain.mate.request.usecase._common.result.AnswerMateRequestDomainResult
import com.qubacy.geoqq.domain.mate.requests.usecase._common.MateRequestsUseCase
import com.qubacy.geoqq.domain.mate.requests.usecase._common.result.chunk.get.GetRequestChunkDomainResult
import com.qubacy.geoqq.domain.mate.requests.usecase._common.result.chunk.update.UpdateRequestChunkDomainResult
import com.qubacy.geoqq.domain.mate.requests.usecase._common.result.request.added.MateRequestAddedDomainResult
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.business.model.BusinessViewModel
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.stateful.model.operation._common.UiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.presentation.user.UserPresentation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.presentation.user.toUserPresentation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.requests._common.presentation.toMateRequestPresentation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.requests.model._common.MateRequestsViewModel
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.requests.model._common.operation.chunk.insert.InsertRequestsUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.requests.model._common.operation.request.RemoveRequestUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.requests.model._common.operation.answer.ReturnAnsweredRequestUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.requests.model._common.operation.chunk.update.UpdateRequestsUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.requests.model._common.operation.request.AddRequestUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.requests.model._common.operation.request.UpdateRequestUiOperation
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Qualifier

open class MateRequestsViewModelImpl @Inject constructor(
    mSavedStateHandle: SavedStateHandle,
    mErrorSource: LocalErrorDatabaseDataSource,
    mMateRequestsUseCase: MateRequestsUseCase
) : MateRequestsViewModel(mSavedStateHandle, mErrorSource, mMateRequestsUseCase) {
    private var mIsGettingNextRequestChunk = false

    override fun getUserProfileWithMateRequestId(id: Long): UserPresentation {
        val user = mUiState.requests.find { it.id == id }!!.user // todo: is it alright?

        mUseCase.getInterlocutor(user.id) // todo: is it necessary?

        return user
    }

    override fun getNextRequestChunk() {
        if (!isNextRequestChunkGettingAllowed()) return

        mIsGettingNextRequestChunk = true

        changeLoadingState(true)

        mUseCase.getRequestChunk(mUiState.requests.size)
    }

    override fun answerRequest(position: Int, isAccepted: Boolean) {
        changeLoadingState(true)

        val request = mUiState.requests[position]

        mUseCase.answerRequest(request.id, isAccepted)
    }

    override fun isNextRequestChunkGettingAllowed(): Boolean {
        val prevRequestCount = mUiState.requests.size + mUiState.answeredRequestCount
        val preparedPrevRequestCount =
            if (prevRequestCount > 0) prevRequestCount - mUiState.newRequestCount
            else prevRequestCount

        val chunkSizeCheck =
            (preparedPrevRequestCount % MateRequestsUseCase.DEFAULT_REQUEST_CHUNK_SIZE == 0)

        return (!mIsGettingNextRequestChunk && chunkSizeCheck)
    }

    override fun onMateRequestsGetRequestChunk(
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

    override fun onMateRequestsUpdateRequestChunk(
        updateRequestChunkResult: UpdateRequestChunkDomainResult
    ): List<UiOperation> {
        if (!updateRequestChunkResult.isSuccessful())
            return onError(updateRequestChunkResult.error!!)

        val requestChunk = updateRequestChunkResult.chunk!!
        val requestChunkPosition = requestChunk.offset
        val updatedRequests = requestChunk.requests.map { it.toMateRequestPresentation() }

        // todo: is it alright?:
        for (i in requestChunkPosition until requestChunkPosition + requestChunk.requests.size) {
            val updatedRequest = updatedRequests[i - requestChunkPosition]

            mUiState.requests[i] = updatedRequest
        }

        return listOf(UpdateRequestsUiOperation(requestChunkPosition, updatedRequests))
    }

    override fun onMateRequestsAnswerMateRequest(
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

    override fun onMateRequestsMateRequestAdded(
        mateRequestAddedDomainResult: MateRequestAddedDomainResult
    ): List<UiOperation> {
        if (!mateRequestAddedDomainResult.isSuccessful())
            return onError(mateRequestAddedDomainResult.error!!)

        val mateRequestPresentation = mateRequestAddedDomainResult
            .request!!.toMateRequestPresentation()

        mUiState.requests.add(mateRequestPresentation)

        return listOf(AddRequestUiOperation(mateRequestPresentation))
    }

    override fun onUserUpdateUser(
        domainResult: UpdateUserDomainResult
    ): List<UiOperation> {
        val superUiOperations = super.onUserUpdateUser(domainResult)

        if (!domainResult.isSuccessful()) return superUiOperations

        val interlocutor = domainResult.interlocutor!!

        val requestPosition = mUiState.requests.indexOfFirst { it.user.id == interlocutor.id }
        val updatedRequest = mUiState.requests.let {
            val request = it[requestPosition].copy(user = interlocutor.toUserPresentation())

            it[requestPosition] = request

            request
        }

        return superUiOperations.plus(UpdateRequestUiOperation(requestPosition, updatedRequest))
    }

    override fun onUserUser(domainResult: UserDomainResult): UserPresentation {
        val userPresentation = domainResult.interlocutor!!.toUserPresentation()

        val requestPosition = mUiState.requests.indexOfFirst { it.user.id == userPresentation.id }

        mUiState.requests[requestPosition] =
            mUiState.requests[requestPosition].copy(user = userPresentation)

        return userPresentation
    }

    override fun getUserViewModelBusinessViewModel(): BusinessViewModel<*, *> {
        return this
    }

    private fun onMateRequestAnsweringFailed(requestPosition: Int) {
        viewModelScope.launch {
            mUiOperationFlow.emit(ReturnAnsweredRequestUiOperation(requestPosition))
        }
    }
}

@Qualifier
annotation class MateRequestsViewModelFactoryQualifier

class MateRequestsViewModelImplFactory @Inject constructor(
    private val mErrorSource: LocalErrorDatabaseDataSource,
    private val mMateRequestsUseCase: MateRequestsUseCase
) : AbstractSavedStateViewModelFactory() {
    override fun <T : ViewModel> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle
    ): T {
        if (!modelClass.isAssignableFrom(MateRequestsViewModelImpl::class.java))
            throw IllegalArgumentException()

        return MateRequestsViewModelImpl(handle, mErrorSource, mMateRequestsUseCase) as T
    }
}