package com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.business

import androidx.viewbinding.ViewBinding
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.stateful.model.operation._common.UiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.business.model.BusinessViewModel
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.business.model.state.BusinessUiState
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.loading.LoadingFragment
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.loading.operation.handler.LoadingUiOperationHandler
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.stateful.StatefulFragment
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.stateful.operation.handler._common.UiOperationHandler

abstract class BusinessFragment<
    ViewBindingType : ViewBinding,
    UiStateType : BusinessUiState,
    ViewModelType : BusinessViewModel<UiStateType, *>
>() : StatefulFragment<ViewBindingType, UiStateType, ViewModelType>(), LoadingFragment {
    companion object {
        const val TAG = "BusinessFragment"
    }

    override val mStartTransitionOnPreDraw: Boolean = false

    override fun generateUiOperationHandlers(): Array<UiOperationHandler<*>> {
        return super.generateUiOperationHandlers()
            .plus(LoadingUiOperationHandler(this))
    }

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

        return super.processUiOperation(uiOperation)
    }

    protected open fun onBackendResponded() {
        if (mModel.backendResponded) return

        mModel.setBackendResponded()

        startPostponedEnterTransition() // todo: is it ok?
    }
}