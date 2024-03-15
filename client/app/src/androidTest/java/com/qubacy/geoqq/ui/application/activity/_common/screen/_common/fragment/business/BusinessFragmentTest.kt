package com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.business

import androidx.viewbinding.ViewBinding
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.stateful.StatefulFragmentTest
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.stateful.model.state.TestUiState
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.business.model.BusinessViewModel
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.business.model.state.BusinessUiState

abstract class BusinessFragmentTest<
    ViewBindingType : ViewBinding,
    UiStateType : BusinessUiState,
    TestUiStateType : TestUiState,
    ViewModelType : BusinessViewModel<UiStateType>,
    FragmentType : BusinessFragment<ViewBindingType, UiStateType, ViewModelType>
>(

) : StatefulFragmentTest<
    ViewBindingType, UiStateType, TestUiStateType, ViewModelType, FragmentType
>() {

}