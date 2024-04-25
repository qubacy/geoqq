package com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.business.model

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.qubacy.geoqq._common.model.error._common.Error
import com.qubacy.geoqq.data.error.repository.ErrorDataRepository
import com.qubacy.geoqq.domain._common.usecase._common.UseCase
import com.qubacy.geoqq.domain._common.usecase._common.result._common.DomainResult
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.stateful.model.StatefulViewModel
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.stateful.model.operation._common.UiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.stateful.model.operation.error.ErrorUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.stateful.model.operation.loading.SetLoadingStateUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.business.model.state.BusinessUiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.launch

abstract class BusinessViewModel<UiStateType : BusinessUiState, UseCaseType : UseCase>(
    mSavedStateHandle: SavedStateHandle,
    mErrorDataRepository: ErrorDataRepository,
    protected val mUseCase: UseCaseType
) : StatefulViewModel<UiStateType>(mSavedStateHandle, mErrorDataRepository) {
    companion object {
        const val TAG = "BusinessViewModel"

        const val BACKEND_RESPONDED_KEY = "backendResponded"
    }

    override val uiOperationFlow = merge(
        mUiOperationFlow,
        mUseCase.resultFlow.mapNotNull { mapDomainResultFlow(it) }
    )
    private lateinit var mBusinessScope: CoroutineScope

    private var mBackendResponded: Boolean = false
    open val backendResponded get() = mBackendResponded

    init {
        mBackendResponded = mSavedStateHandle[BACKEND_RESPONDED_KEY] ?: false

        resetBusinessScope()
    }

    private fun resetBusinessScope() {
        mBusinessScope = CoroutineScope(viewModelScope.coroutineContext)

        mUseCase.setCoroutineScope(mBusinessScope)
    }

    override fun onCleared() {
        mBusinessScope.cancel()

        super.onCleared()
    }

    fun interrupt() {
        mBusinessScope.cancel()

        resetBusinessScope()
    }

    private fun mapDomainResultFlow(domainResult: DomainResult): UiOperation? {
        return processDomainResultFlow(domainResult)
    }

    protected open fun processDomainResultFlow(domainResult: DomainResult): UiOperation? {
        return null
    }

    protected fun processErrorDomainResult(error: Error): ErrorUiOperation {
        mUiState.error = error

        return ErrorUiOperation(error)
    }

    fun changeLoadingState(isLoading: Boolean) {
        mUiState.isLoading = isLoading

        viewModelScope.launch {
            mUiOperationFlow.emit(SetLoadingStateUiOperation(isLoading))
        }
    }

    open fun setBackendResponded() {
        changeBackendResponded(true)
    }

    open fun prepareForNavigation() {
        changeBackendResponded(false)
    }

    private fun changeBackendResponded(backendResponded: Boolean) {
        mSavedStateHandle[BACKEND_RESPONDED_KEY] = backendResponded
        mBackendResponded = backendResponded
    }
}