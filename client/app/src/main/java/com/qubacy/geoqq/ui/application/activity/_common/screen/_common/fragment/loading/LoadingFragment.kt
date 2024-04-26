package com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.loading

import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.loading.model.operation.SetLoadingStateUiOperation

interface LoadingFragment {
    fun processSetLoadingOperation(loadingOperation: SetLoadingStateUiOperation) {
        adjustUiWithLoadingState(loadingOperation.isLoading)
    }

    fun adjustUiWithLoadingState(isLoading: Boolean) { }
}