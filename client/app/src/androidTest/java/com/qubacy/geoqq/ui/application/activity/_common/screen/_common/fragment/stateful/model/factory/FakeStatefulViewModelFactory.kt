package com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.stateful.model.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.qubacy.geoqq._common._test.util.mock.AnyMockUtil
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.stateful.model.StatefulViewModel
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.stateful.model.state.BaseUiState
import kotlinx.coroutines.flow.MutableSharedFlow
import org.mockito.Mockito

abstract class FakeStatefulViewModelFactory<UiStateType : BaseUiState>(

) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val viewModelMock = Mockito.mock(modelClass) as StatefulViewModel<UiStateType>

        Mockito.`when`(viewModelMock.uiOperationFlow).thenReturn(MutableSharedFlow())
        Mockito.`when`(viewModelMock.retrieveError(AnyMockUtil.anyObject())).thenAnswer {

        }

        return viewModelMock as T
    }
}