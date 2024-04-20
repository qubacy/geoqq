package com.qubacy.geoqq.ui.application.activity._common.screen.mate.requests.model.factory

import androidx.lifecycle.ViewModel
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.stateful.model.factory.FakeStatefulViewModelFactory
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.requests.model.MateRequestsViewModel
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.requests.model.factory._test.mock.MateRequestsViewModelMockContext
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.requests.model.state.MateRequestsUiState
import org.mockito.Mockito

class FakeMateRequestsViewModelFactory(
    mockContext: MateRequestsViewModelMockContext
) : FakeStatefulViewModelFactory<
    MateRequestsUiState, MateRequestsViewModel, MateRequestsViewModelMockContext
>(mockContext) {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val viewModelMock = super.create(modelClass) as MateRequestsViewModel

        Mockito.`when`(viewModelMock.getNextRequestChunk()).thenAnswer {
            mockContext.getNextRequestChunkCallFlag = true

            Unit
        }
        Mockito.`when`(viewModelMock.getUserProfileWithMateRequestId(Mockito.anyLong())).thenAnswer {
            mockContext.getUserProfileWithMateRequestIdCallFlag = true
            mockContext.getUserProfileWithMateRequestId
        }
        Mockito.`when`(viewModelMock.answerRequest(
            Mockito.anyInt(), Mockito.anyBoolean()
        )).thenAnswer {
            mockContext.answerRequestCallFlag = true

            Unit
        }
        Mockito.`when`(viewModelMock.resetRequests()).thenAnswer {
            mockContext.resetRequestsCallFlag = true

            Unit
        }

        return viewModelMock as T
    }
}