package com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.business.model

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.qubacy.geoqq._common.model.error._common.Error
import com.qubacy.geoqq.data.error.repository.ErrorDataRepository
import com.qubacy.geoqq.domain._common.usecase._common.UseCase
import com.qubacy.geoqq.domain._common.usecase._common.result._common.DomainResult
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.stateful.model.StatefulViewModel
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.stateful.model.operation._common.UiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.stateful.model.operation.error.ErrorUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.business.model.state.BusinessUiState
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.loading.model.LoadingViewModel
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.loading.model.extension.changeLoadingState
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.loading.model.extension.preserveLoadingState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.merge

abstract class BusinessViewModel<UiStateType : BusinessUiState, UseCaseType : UseCase>(
    mSavedStateHandle: SavedStateHandle,
    mErrorDataRepository: ErrorDataRepository,
    protected val mUseCase: UseCaseType
) : StatefulViewModel<UiStateType>(mSavedStateHandle, mErrorDataRepository), LoadingViewModel {
    companion object {
        const val TAG = "BusinessViewModel"

        const val BACKEND_RESPONDED_KEY = "backendResponded"
    }

    override val uiOperationFlow = merge(
        mUiOperationFlow,
        mUseCase.resultFlow.flatMapMerge { mapDomainResultFlow(it) }
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

    private suspend fun mapDomainResultFlow(domainResult: DomainResult): Flow<UiOperation> {
        return flow {
            processDomainResultFlow(domainResult).forEach {
                this.emit(it)
            }
        }
    }

    protected open fun processDomainResultFlow(domainResult: DomainResult): List<UiOperation> {
        return listOf()
    }

    protected open fun processErrorDomainResult(error: Error): List<UiOperation> {
        mUiState.error = error

        return listOf(ErrorUiOperation(error))
    }

    override fun preserveLoadingState(isLoading: Boolean) {
        preserveLoadingState(isLoading, mUiState)
    }

    override fun changeLoadingState(isLoading: Boolean) {
        changeLoadingState(isLoading, mUiState, mUiOperationFlow)
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