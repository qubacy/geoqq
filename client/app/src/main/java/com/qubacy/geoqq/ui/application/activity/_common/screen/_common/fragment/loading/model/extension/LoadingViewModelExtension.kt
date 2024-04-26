package com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.loading.model.extension

import androidx.lifecycle.viewModelScope
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.loading.model.state.LoadingUiState
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.stateful.model.StatefulViewModel
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.stateful.model.operation._common.UiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.loading.model.operation.SetLoadingStateUiOperation
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

fun StatefulViewModel<*>.changeLoadingState(
    isLoading: Boolean,
    uiState: LoadingUiState,
    uiOperationFlow: MutableSharedFlow<UiOperation>
) {
    uiState.isLoading = isLoading

    viewModelScope.launch {
        uiOperationFlow.emit(SetLoadingStateUiOperation(isLoading))
    }
}