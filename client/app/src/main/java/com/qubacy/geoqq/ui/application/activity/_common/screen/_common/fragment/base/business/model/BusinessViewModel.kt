package com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.business.model

import android.util.Log
import androidx.annotation.CallSuper
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.qubacy.geoqq._common.model.error._common.Error
import com.qubacy.geoqq.data._common.repository._common.source.local.database.error._common.LocalErrorDatabaseDataSource
import com.qubacy.geoqq.domain._common.usecase.base._common.UseCase
import com.qubacy.geoqq.domain._common.usecase._common.result._common.DomainResult
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.stateful.model.StatefulViewModel
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.stateful.model.operation._common.UiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.stateful.model.operation.error.ErrorUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.business.model.state.BusinessUiState
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.loading.model.LoadingViewModel
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.loading.model.extension.changeLoadingState
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.loading.model.extension.preserveLoadingState
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.business.model.result.handler._common.DomainResultHandler
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.business.model.result.handler.failure.FailureDomainResultHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.merge

abstract class BusinessViewModel<UiStateType : BusinessUiState, UseCaseType : UseCase>(
    mSavedStateHandle: SavedStateHandle,
    mErrorSource: LocalErrorDatabaseDataSource,
    protected val mUseCase: UseCaseType
) : StatefulViewModel<UiStateType>(mSavedStateHandle, mErrorSource),
    LoadingViewModel
{
    companion object {
        const val TAG = "BusinessViewModel"

        const val BACKEND_RESPONDED_KEY = "backendResponded"
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override val uiOperationFlow = merge(
        mUiOperationFlow,
        mUseCase.resultFlow.flatMapMerge(1) { mapDomainResultFlow(it) }
    )
    private lateinit var mBusinessScope: CoroutineScope

    private var mBackendResponded: Boolean = false
    open val backendResponded get() = mBackendResponded

    protected val mDomainResultHandlers: Array<DomainResultHandler<*>>

    @CallSuper
    protected open fun generateDomainResultHandlers(): Array<DomainResultHandler<*>> {
        return arrayOf(FailureDomainResultHandler(this))
    }

    init {
        mBackendResponded = mSavedStateHandle[BACKEND_RESPONDED_KEY] ?: false

        resetBusinessScope()

        mDomainResultHandlers = generateDomainResultHandlers()
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
        Log.d(TAG, "processDomainResultFlow(): domainResult = $domainResult;")

        for (domainResultHandler in mDomainResultHandlers) {
            val uiOperations = domainResultHandler.handleDomainResult(domainResult)

            if (uiOperations.isNotEmpty()) return uiOperations
        }

        return listOf()
    }

    fun onError(error: Error): List<UiOperation> {
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