package com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.business

import androidx.viewbinding.ViewBinding
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.stateful.model.operation._common.UiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.loading.model.operation.SetLoadingStateUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.business.model.BusinessViewModel
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.business.model.state.BusinessUiState
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.loading.LoadingFragment
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.stateful.StatefulFragment

abstract class BusinessFragment<
    ViewBindingType : ViewBinding,
    UiStateType : BusinessUiState,
    ViewModelType : BusinessViewModel<UiStateType, *>
>() : StatefulFragment<ViewBindingType, UiStateType, ViewModelType>(), LoadingFragment {
    companion object {
        const val TAG = "BusinessFragment"
    }

    override val mStartTransitionOnPreDraw: Boolean = false

    override fun afterDestinationChange() {
        super.afterDestinationChange()

        this@BusinessFragment.adjustUiWithLoadingState(true)
        mModel.prepareForNavigation() // todo: rethink this;
    }

    protected override fun runInitWithUiState(uiState: UiStateType) {
        super.runInitWithUiState(uiState)

        if (uiState.isLoading) adjustUiWithLoadingState(true)
    }

    protected override fun processUiOperation(uiOperation: UiOperation): Boolean {
        onBackendResponded()

        if (super.processUiOperation(uiOperation)) return true

        when (uiOperation::class) {
            SetLoadingStateUiOperation::class ->
                processSetLoadingOperation(uiOperation as SetLoadingStateUiOperation)
            else -> return false
        }

        return true
    }

    protected open fun onBackendResponded() {
        if (mModel.backendResponded) return

        mModel.setBackendResponded()

        startPostponedEnterTransition() // todo: is it ok?
    }
}