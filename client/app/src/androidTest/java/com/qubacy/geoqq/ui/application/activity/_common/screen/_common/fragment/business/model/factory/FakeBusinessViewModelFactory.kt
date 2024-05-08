package com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.business.model.factory

import androidx.lifecycle.ViewModel
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.business.model.BusinessViewModel
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.business.model.state.BusinessUiState
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.business.model.factory._test.mock.BusinessViewModelMockContext
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.stateful.model.factory.FakeStatefulViewModelFactory
import org.mockito.Mockito

abstract class FakeBusinessViewModelFactory<
    UiStateType : BusinessUiState,
    ViewModelType : BusinessViewModel<UiStateType, *>,
    ViewModelMockContextType : BusinessViewModelMockContext<UiStateType>
>(
    mockContext: ViewModelMockContextType
) : FakeStatefulViewModelFactory<
    UiStateType, ViewModelType, ViewModelMockContextType
>(mockContext) {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val viewModelMock = super.create(modelClass) as ViewModelType

        Mockito.`when`(viewModelMock.setBackendResponded()).thenAnswer {
            mockContext.setBackendRespondedCallFlag = true

            Unit
        }
        Mockito.`when`(viewModelMock.backendResponded).thenAnswer {
            mockContext.backendResponded
        }

        return viewModelMock as T
    }
}