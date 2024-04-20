package com.qubacy.geoqq.ui.application.activity._common.screen.mate.chat.model.factory

import androidx.lifecycle.ViewModel
import com.qubacy.geoqq._common._test.util.mock.AnyMockUtil
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.stateful.model.StatefulViewModel
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.stateful.model.factory.FakeStatefulViewModelFactory
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chat.model.MateChatViewModel
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chat.model.factory._test.mock.MateChatViewModelMockContext
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chat.model.state.MateChatUiState
import org.mockito.Mockito

class FakeMateChatViewModelFactory(
    mockContext: MateChatViewModelMockContext
) : FakeStatefulViewModelFactory<
    MateChatUiState, MateChatViewModel, MateChatViewModelMockContext
>(mockContext) {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val viewModelMock = super.create(modelClass) as MateChatViewModel

        StatefulViewModel::class.java.getDeclaredField("mUiState")
            .apply { isAccessible = true }
            .set(viewModelMock, mockContext.uiState)
        Mockito.`when`(viewModelMock.setChatContext(AnyMockUtil.anyObject())).thenAnswer {
            mockContext.setChatContextCallFlag = true

            Unit
        }
        Mockito.`when`(viewModelMock.getNextMessageChunk()).thenAnswer {
            mockContext.uiState.isLoading = true
            mockContext.getNextMessageChunkCallFlag = true

            Unit
        }
        Mockito.`when`(viewModelMock.isInterlocutorMate(AnyMockUtil.anyObject())).thenAnswer {
            mockContext.isInterlocutorMate
        }
        Mockito.`when`(viewModelMock.isInterlocutorChatable(AnyMockUtil.anyObject())).thenAnswer {
            mockContext.isInterlocutorChatable
        }
        Mockito.`when`(viewModelMock.isInterlocutorMateable(AnyMockUtil.anyObject())).thenAnswer {
            mockContext.isInterlocutorMateable
        }
        Mockito.`when`(viewModelMock.isInterlocutorMateableOrDeletable(AnyMockUtil.anyObject())).thenAnswer {
            mockContext.isInterlocutorMateableOrDeletable
        }
        Mockito.`when`(viewModelMock.isChatDeletable(AnyMockUtil.anyObject())).thenAnswer {
            mockContext.isChatDeletable
        }
        Mockito.`when`(viewModelMock.getInterlocutorProfile()).thenAnswer {
            mockContext.getInterlocutorProfileCallFlag = true
            mockContext.getInterlocutorProfile
        }
        Mockito.`when`(viewModelMock.addInterlocutorAsMate()).thenAnswer {
            mockContext.uiState.isLoading = true
            mockContext.addInterlocutorAsMateCallFlag = true

            Unit
        }
        Mockito.`when`(viewModelMock.deleteChat()).thenAnswer {
            mockContext.uiState.isLoading = true
            mockContext.deleteChatCallFlag = true

            Unit
        }

        return viewModelMock as T
    }
}