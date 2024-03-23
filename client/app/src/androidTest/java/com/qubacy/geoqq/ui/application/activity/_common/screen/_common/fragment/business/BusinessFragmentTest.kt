package com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.business

import androidx.viewbinding.ViewBinding
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.stateful.StatefulFragmentTest
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.business.model.BusinessViewModel
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.business.model.state.BusinessUiState
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.stateful.model.factory._test.mock.ViewModelMockContext

abstract class BusinessFragmentTest<
    ViewBindingType : ViewBinding,
    UiStateType : BusinessUiState,
    ViewModelType : BusinessViewModel<UiStateType, *>,
    ViewModelMockContextType : ViewModelMockContext<UiStateType>,
    FragmentType : BusinessFragment<ViewBindingType, UiStateType, ViewModelType>
>(

) : StatefulFragmentTest<
    ViewBindingType, UiStateType, ViewModelType, ViewModelMockContextType, FragmentType
>() {

}