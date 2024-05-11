package com.qubacy.geoqq.ui.application.activity._common.screen.mate.chats.model.factory

import androidx.lifecycle.ViewModel
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.business.model.factory.FakeBusinessViewModelFactory
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chats.model.impl.MateChatsViewModelImpl
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chats.model.factory._test.mock.MateChatsViewModelMockContext
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chats.model._common.state.MateChatsUiState
import org.mockito.Mockito

class FakeMateChatsViewModelFactory(
    mockContext: MateChatsViewModelMockContext
) : FakeBusinessViewModelFactory<
        MateChatsUiState, MateChatsViewModelImpl, MateChatsViewModelMockContext
>(mockContext) {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val viewModelMock = super.create(modelClass) as MateChatsViewModelImpl

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