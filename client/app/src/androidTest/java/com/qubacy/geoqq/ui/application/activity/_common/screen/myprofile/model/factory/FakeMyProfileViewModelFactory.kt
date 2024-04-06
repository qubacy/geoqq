package com.qubacy.geoqq.ui.application.activity._common.screen.myprofile.model.factory

import androidx.lifecycle.ViewModel
import com.qubacy.geoqq._common._test.util.mock.AnyMockUtil
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.stateful.model.factory.FakeStatefulViewModelFactory
import com.qubacy.geoqq.ui.application.activity._common.screen.myprofile.model.MyProfileViewModel
import com.qubacy.geoqq.ui.application.activity._common.screen.myprofile.model.factory._test.mock.MyProfileViewModelMockContext
import com.qubacy.geoqq.ui.application.activity._common.screen.myprofile.model.state.MyProfileUiState
import org.mockito.Mockito

class FakeMyProfileViewModelFactory(
    mockContext: MyProfileViewModelMockContext
) : FakeStatefulViewModelFactory<
    MyProfileUiState, MyProfileViewModel, MyProfileViewModelMockContext
>(mockContext) {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val viewModelMock = super.create(modelClass) as MyProfileViewModel

        Mockito.`when`(viewModelMock.preserveInputData(AnyMockUtil.anyObject())).thenAnswer {
            mockContext.uiState.isLoading = true
            mockContext.preserveInputDataCallFlag = true

            Unit
        }
        Mockito.`when`(viewModelMock.isUpdateDataValid(AnyMockUtil.anyObject())).thenAnswer {
            mockContext.uiState.isLoading = true
            mockContext.isUpdateDataValidCallFlag = true

            mockContext.isUpdateDataValid
        }
        Mockito.`when`(viewModelMock.getMyProfile()).thenAnswer {
            mockContext.uiState.isLoading = true
            mockContext.getMyProfileCallFlag = true

            Unit
        }
        Mockito.`when`(viewModelMock.updateMyProfile(AnyMockUtil.anyObject())).thenAnswer {
            mockContext.uiState.isLoading = true
            mockContext.updateMyProfileCallFlag = true

            Unit
        }
        Mockito.`when`(viewModelMock.deleteMyProfile()).thenAnswer {
            mockContext.uiState.isLoading = true
            mockContext.deleteMyProfileCallFlag = true

            Unit
        }
        Mockito.`when`(viewModelMock.logout()).thenAnswer {
            mockContext.uiState.isLoading = true
            mockContext.logoutMyProfileCallFlag = true

            Unit
        }

        return viewModelMock as T
    }
}