package com.qubacy.geoqq.ui.application.activity._common.screen.geo.chat.model.factory

import androidx.lifecycle.ViewModel
import com.qubacy.geoqq._common._test.util.mock.AnyMockUtil
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.business.model.factory.FakeBusinessViewModelFactory
import com.qubacy.geoqq.ui.application.activity._common.screen.geo.chat.model._common.GeoChatViewModel
import com.qubacy.geoqq.ui.application.activity._common.screen.geo.chat.model.factory._test.mock.GeoChatViewModelMockContext
import com.qubacy.geoqq.ui.application.activity._common.screen.geo.chat.model._common.state.GeoChatUiState
import org.mockito.Mockito

class FakeGeoChatViewModelFactory(
    mockContext: GeoChatViewModelMockContext
) : FakeBusinessViewModelFactory<
        GeoChatUiState, GeoChatViewModel, GeoChatViewModelMockContext
        >(mockContext) {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val viewModelMock = super.create(modelClass) as GeoChatViewModel

        Mockito.`when`(viewModelMock.changeLastLocation(AnyMockUtil.anyObject())).thenAnswer {
            mockContext.changeLastLocationCallFlag = true

            Unit
        }
        Mockito.`when`(viewModelMock.setLocationContext(
            Mockito.anyInt(), Mockito.anyFloat(), Mockito.anyFloat()
        )).thenAnswer {
            mockContext.setLocationContextCallFlag = true

            Unit
        }
        Mockito.`when`(viewModelMock.isLocationContextSet()).thenAnswer {
            mockContext.isLocationContextSetCallFlag = true
            mockContext.isLocationContextSet
        }
        Mockito.`when`(viewModelMock.getLocalUserId()).thenAnswer {
            mockContext.getLocalUserIdCallFlag = true
            mockContext.getLocalUserId
        }
        Mockito.`when`(viewModelMock.getMessages()).thenAnswer {
            mockContext.getMessagesCallFlag = true

            Unit
        }
        Mockito.`when`(viewModelMock.areMessagesLoaded()).thenAnswer {
            mockContext.areMessagesLoadedCallFlag = true
            mockContext.areMessagesLoaded
        }
        Mockito.`when`(viewModelMock.isMessageTextValid(Mockito.anyString())).thenAnswer {
            mockContext.isMessageTextValidCallFlag = true
            mockContext.isMessageTextValid
        }
        Mockito.`when`(viewModelMock.getUserProfileByMessagePosition(Mockito.anyInt())).thenAnswer {
            mockContext.getUserProfileByMessagePositionCallFlag = true

            Unit
        }
        Mockito.`when`(viewModelMock.addInterlocutorAsMate(Mockito.anyLong())).thenAnswer {
            mockContext.addInterlocutorAsMateCallFlag = true

            Unit
        }
        Mockito.`when`(viewModelMock.sendMessage(Mockito.anyString())).thenAnswer {
            mockContext.sendMessageCallFlag = true

            Unit
        }
        Mockito.`when`(viewModelMock.resetMessages()).thenAnswer {
            mockContext.resetMessagesCallFlag = true

            Unit
        }

        return viewModelMock as T
    }
}