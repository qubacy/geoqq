package com.qubacy.geoqq.ui.application.activity._common.screen.myprofile.model.module

import androidx.lifecycle.ViewModelProvider
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.stateful.model.module.FakeViewModelModule
import com.qubacy.geoqq.ui.application.activity._common.screen.myprofile.model.impl.MyProfileViewModelFactoryQualifier
import com.qubacy.geoqq.ui.application.activity._common.screen.myprofile.model.factory.FakeMyProfileViewModelFactory
import com.qubacy.geoqq.ui.application.activity._common.screen.myprofile.model.factory._test.mock.MyProfileViewModelMockContext
import com.qubacy.geoqq.ui.application.activity._common.screen.myprofile.model._common.state.MyProfileUiState
import dagger.Module
import dagger.Provides

@Module
object FakeMyProfileViewModelModule : FakeViewModelModule<
    MyProfileUiState, MyProfileViewModelMockContext
>() {
    @Provides
    @MyProfileViewModelFactoryQualifier
    fun provideFakeMyProfileViewModelFactory(): ViewModelProvider.Factory {
        return FakeMyProfileViewModelFactory(mockContext)
    }
}