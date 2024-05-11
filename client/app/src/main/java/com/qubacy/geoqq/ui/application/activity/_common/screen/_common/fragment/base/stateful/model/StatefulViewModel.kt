package com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.stateful.model

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.qubacy.geoqq._common.model.error._common.type.ErrorType
import com.qubacy.geoqq.data._common.repository._common.source.local.database.error._common.LocalErrorDatabaseDataSource
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.stateful.model.operation._common.UiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.stateful.model.operation.error.ErrorUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.stateful.model.state.BaseUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

abstract class StatefulViewModel<UiStateType: BaseUiState>(
    protected val mSavedStateHandle: SavedStateHandle,
    protected val mErrorSource: LocalErrorDatabaseDataSource
) : ViewModel() {
    companion object {
        const val UI_STATE_KEY = "uiState"

        const val TAG = "StatefulViewModel"
    }

    protected val mUiOperationFlow = MutableSharedFlow<UiOperation>()
    open val uiOperationFlow: Flow<UiOperation> = mUiOperationFlow

    protected var mUiState: UiStateType
    open val uiState: UiStateType get() = mUiState.copy() as UiStateType

    init {
        mUiState = mSavedStateHandle[UI_STATE_KEY] ?: generateDefaultUiState()
    }

    override fun onCleared() {
        mSavedStateHandle[UI_STATE_KEY] = mUiState

        super.onCleared()
    }

    protected abstract fun generateDefaultUiState() : UiStateType

    open fun retrieveError(errorType: ErrorType) {
        viewModelScope.launch(Dispatchers.Default) {
            val error = mErrorSource.getError(errorType.getErrorCode())

            mUiOperationFlow.emit(ErrorUiOperation(error))
        }
    }

    open fun absorbCurrentError() {
        mUiState.error = null
    }
}