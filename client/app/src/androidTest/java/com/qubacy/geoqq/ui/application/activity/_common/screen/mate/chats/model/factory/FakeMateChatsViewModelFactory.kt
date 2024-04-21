package com.qubacy.geoqq.ui.application.activity._common.screen.mate.chats.model.factory

import androidx.lifecycle.ViewModel
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.stateful.model.factory.FakeStatefulViewModelFactory
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chats.model.MateChatsViewModel
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chats.model.factory._test.mock.MateChatsViewModelMockContext
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chats.model.state.MateChatsUiState
import org.mockito.Mockito

class FakeMateChatsViewModelFactory(
    mockContext: MateChatsViewModelMockContext
) : FakeStatefulViewModelFactory<
    MateChatsUiState, MateChatsViewModel, MateChatsViewModelMockContext
>(mockContext) {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val viewModelMock = super.create(modelClass) as MateChatsViewModel

        Mockito.`when`(viewModelMock.getNextChatChunk()).thenAnswer {
            mockContext.getNextChatChunkCallFlag = true

            Unit
        }
        Mockito.`when`(viewModelMock.prepareChatForEntering(Mockito.anyLong())).thenAnswer {
            mockContext.prepareChatForEnteringCallFlag = true
            mockContext.prepareChatForEntering
        }

        return viewModelMock as T
    }
}