package com.qubacy.geoqq.ui.application.activity._common.screen.myprofile.model.state

import com.qubacy.geoqq._common.model.error.Error
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.business.model.state.BusinessUiState
import com.qubacy.geoqq.ui.application.activity._common.screen.myprofile.model.state.input.MyProfileInputData

class MyProfileUiState(
    isLoading: Boolean = false,
    error: Error? = null,
    var myProfileInputData: MyProfileInputData = MyProfileInputData()
) : BusinessUiState(isLoading, error) {
    override fun copy(): MyProfileUiState {
        return MyProfileUiState(isLoading, error?.copy(), myProfileInputData.copy())
    }
}