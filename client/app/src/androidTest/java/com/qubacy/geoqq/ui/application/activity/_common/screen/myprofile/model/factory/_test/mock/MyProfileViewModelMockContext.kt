package com.qubacy.geoqq.ui.application.activity._common.screen.myprofile.model.factory._test.mock

import com.qubacy.geoqq._common.model.error._common.Error
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.stateful.model.factory._test.mock.ViewModelMockContext
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.stateful.model.operation._common.UiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen.myprofile.model.state.MyProfileUiState
import kotlinx.coroutines.flow.MutableSharedFlow

class MyProfileViewModelMockContext(
    uiState: MyProfileUiState,
    uiOperationFlow: MutableSharedFlow<UiOperation> = MutableSharedFlow(),
    retrieveErrorResult: Error? = null,
    var isUpdateDataValid: Boolean = false,
    var preserveInputDataCallFlag: Boolean = false,
    var isUpdateDataValidCallFlag: Boolean = false,
    var getMyProfileCallFlag: Boolean = false,
    var updateMyProfileCallFlag: Boolean = false,
    var deleteMyProfileCallFlag: Boolean = false,
    var logoutMyProfileCallFlag: Boolean = false,
) : ViewModelMockContext<MyProfileUiState>(uiState, uiOperationFlow, retrieveErrorResult) {
    override fun reset() {
        super.reset()

        uiState = MyProfileUiState()

        preserveInputDataCallFlag = false
        isUpdateDataValidCallFlag = false
        getMyProfileCallFlag = false
        updateMyProfileCallFlag = false
        deleteMyProfileCallFlag = false
        logoutMyProfileCallFlag = false
    }
}