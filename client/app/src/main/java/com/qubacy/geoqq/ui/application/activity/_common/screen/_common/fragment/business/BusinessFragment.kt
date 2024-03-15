package com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.business

import androidx.viewbinding.ViewBinding
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.stateful.model.operation._common.UiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.stateful.model.operation.loading.SetLoadingStateUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.business.model.BusinessViewModel
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.business.model.state.BusinessUiState
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.stateful.StatefulFragment

abstract class BusinessFragment<
    ViewBindingType : ViewBinding,
    UiStateType : BusinessUiState,
    ViewModelType : BusinessViewModel<UiStateType>
>() : StatefulFragment<ViewBindingType, UiStateType, ViewModelType>() {
    companion object {
        const val TAG = "BusinessFragment"
    }

    protected override fun runInitWithUiState(uiState: UiStateType) {
        super.runInitWithUiState(uiState)

        if (uiState.isLoading) setLoadingState(true)
    }

    protected override fun processUiOperation(uiOperation: UiOperation): Boolean {
        if (super.processUiOperation(uiOperation)) return true

        when (uiOperation::class) {
            SetLoadingStateUiOperation::class ->
                processSetLoadingOperation(uiOperation as SetLoadingStateUiOperation)

            else -> return false
        }

        return true
    }

    protected open fun processSetLoadingOperation(loadingOperation: SetLoadingStateUiOperation) {}

    protected open fun setLoadingState(isLoading: Boolean) {}
}