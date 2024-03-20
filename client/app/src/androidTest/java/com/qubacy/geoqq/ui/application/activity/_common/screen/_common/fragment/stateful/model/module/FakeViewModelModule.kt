package com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.stateful.model.module

import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.stateful.model.factory._test.mock.ViewModelMockContext
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.stateful.model.state.BaseUiState

abstract class FakeViewModelModule<
    UiStateType : BaseUiState,
    ViewModelMockContextType : ViewModelMockContext<UiStateType>
> {
    @Volatile
    lateinit var mockContext: ViewModelMockContextType
}