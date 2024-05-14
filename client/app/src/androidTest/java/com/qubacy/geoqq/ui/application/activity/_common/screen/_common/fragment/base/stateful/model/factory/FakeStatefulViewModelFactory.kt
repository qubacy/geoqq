package com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.stateful.model.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.qubacy.geoqq._common._test.util.mock.AnyMockUtil
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.stateful.model.StatefulViewModel
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.stateful.model.operation.error.ErrorUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.stateful.model.state.BaseUiState
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.stateful.model.factory._test.mock.ViewModelMockContext
import kotlinx.coroutines.test.runTest
import org.mockito.Mockito

abstract class FakeStatefulViewModelFactory<
    UiStateType : BaseUiState,
    ViewModelType : StatefulViewModel<UiStateType>,
    ViewModelMockContextType : ViewModelMockContext<UiStateType>
>(
    val mockContext: ViewModelMockContextType
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val viewModelMock = Mockito.mock(modelClass) as ViewModelType

        Mockito.`when`(viewModelMock.uiState).thenReturn(mockContext.uiState)
        Mockito.`when`(viewModelMock.uiOperationFlow).thenReturn(mockContext.uiOperationFlow)
        Mockito.`when`(viewModelMock.retrieveError(AnyMockUtil.anyObject())).thenAnswer {
            runTest {
                val errorOperation = ErrorUiOperation(mockContext.retrieveErrorResult!!)

                mockContext.uiOperationFlow.emit(errorOperation)
            }

            Unit
        }

        return viewModelMock as T
    }
}