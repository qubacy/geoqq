package com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.business

import androidx.test.core.app.ActivityScenario
import androidx.viewbinding.ViewBinding
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.loading.LoadingFragmentTest
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.business.BusinessFragment
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.business.model.BusinessViewModel
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.business.model.state.BusinessUiState
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.stateful.StatefulFragmentTest
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
>(), LoadingFragmentTest<FragmentType> {
    override fun getLoadingFragmentFragment(): FragmentType {
        return mFragment
    }

    override fun getLoadingFragmentActivityScenario(): ActivityScenario<*> {
        return mActivityScenario
    }

    override fun beforeAdjustUiWithLoadingStateTest() {
        defaultInit()
    }
}