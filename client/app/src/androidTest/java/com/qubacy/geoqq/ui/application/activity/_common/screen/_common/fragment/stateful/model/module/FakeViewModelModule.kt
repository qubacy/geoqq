package com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.stateful.model.module

import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.stateful.model.state.BaseUiState
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.stateful.model.factory._test.mock.ViewModelMockContext

abstract class FakeViewModelModule<
    UiStateType : BaseUiState,
    ViewModelMockContextType : ViewModelMockContext<UiStateType>
> {
    @Volatile
    lateinit var mockContext: ViewModelMockContextType
}