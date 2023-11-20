package com.qubacy.geoqq.ui.screen.mate.request.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.qubacy.geoqq.domain.common.operation.error.HandleErrorOperation
import com.qubacy.geoqq.domain.common.operation.common.Operation
import com.qubacy.geoqq.domain.mate.request.MateRequestsUseCase
import com.qubacy.geoqq.domain.mate.request.model.MateRequest
import com.qubacy.geoqq.domain.mate.request.operation.MateRequestAnswerProcessedOperation
import com.qubacy.geoqq.domain.mate.request.operation.SetMateRequestsOperation
import com.qubacy.geoqq.domain.mate.request.state.MateRequestsState
import com.qubacy.geoqq.ui.common.fragment.common.base.model.operation.ShowErrorUiOperation
import com.qubacy.geoqq.ui.common.fragment.common.base.model.operation.common.UiOperation
import com.qubacy.geoqq.ui.common.fragment.waiting.model.WaitingViewModel
import com.qubacy.geoqq.ui.screen.mate.request.model.operation.MateRequestAnswerProcessedUiOperation
import com.qubacy.geoqq.ui.screen.mate.request.model.operation.SetMateRequestsUiOperation
import com.qubacy.geoqq.ui.screen.mate.request.model.state.MateRequestsUiState
import kotlinx.coroutines.flow.map

class MateRequestsViewModel(
    val mateRequestsUseCase: MateRequestsUseCase
) : WaitingViewModel() {
    companion object {
        const val DEFAULT_REQUEST_CHUNK_SIZE = 20
    }

    private val mMateRequestsStateFlow = mateRequestsUseCase.stateFlow

    private val mMateRequestsUiStateFlow = mMateRequestsStateFlow.map { stateToUiState(it) }
    val mateRequestFlow: LiveData<MateRequestsUiState?> = mMateRequestsUiStateFlow.asLiveData()

    init {
        mateRequestsUseCase.setCoroutineScope(viewModelScope)
    }

    fun acceptMateRequest(mateRequest: MateRequest) {
        isWaiting.value = true

        mateRequestsUseCase.answerMateRequest(mateRequest.id, true)
    }

    fun declineMateRequest(mateRequest: MateRequest) {
        isWaiting.value = true

        mateRequestsUseCase.answerMateRequest(mateRequest.id, false)
    }

    fun getMateRequests() {
        isWaiting.value = true

        mateRequestsUseCase.getMateRequests(DEFAULT_REQUEST_CHUNK_SIZE)
    }

    private fun stateToUiState(state: MateRequestsState?): MateRequestsUiState? {
        if (isWaiting.value == true) isWaiting.value = false // todo: should it be this way?
        if (state == null) return null

        val uiOperations = mutableListOf<UiOperation>()

        for (operation in state.newOperations) {
            val uiOperation = processOperation(operation)

            if (uiOperation == null) continue

            uiOperations.add(uiOperation)
        }

        return MateRequestsUiState(state.mateRequests, state.users, uiOperations)
    }

    private fun processOperation(operation: Operation): UiOperation? {
        return when (operation::class) {
            SetMateRequestsOperation::class -> {
                val setMateRequestsOperation = operation as SetMateRequestsOperation

                SetMateRequestsUiOperation()
            }
            MateRequestAnswerProcessedOperation::class -> {
                val mateRequestAnswerProcessedOperation =
                    operation as MateRequestAnswerProcessedOperation

                MateRequestAnswerProcessedUiOperation()
            }
            HandleErrorOperation::class -> {
                val handleErrorOperation = operation as HandleErrorOperation

                return ShowErrorUiOperation(handleErrorOperation.error)
            }
            else -> {
                throw IllegalStateException()
            }
        }
    }

    override fun retrieveError(errorId: Long) {
        TODO("Not yet implemented")
    }
}

class MateRequestsViewModelFactory(
    val mateRequestsUseCase: MateRequestsUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (!modelClass.isAssignableFrom(MateRequestsViewModel::class.java))
            throw IllegalArgumentException()

        return MateRequestsViewModel(mateRequestsUseCase) as T
    }
}