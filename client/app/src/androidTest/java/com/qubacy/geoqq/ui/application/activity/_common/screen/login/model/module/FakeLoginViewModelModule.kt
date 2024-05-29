package com.qubacy.geoqq.ui.application.activity._common.screen.login.model.module

import androidx.lifecycle.ViewModelProvider
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.stateful.model.module.FakeViewModelModule
import com.qubacy.geoqq.ui.application.activity._common.screen.login.model.impl.LoginViewModelFactoryQualifier
import com.qubacy.geoqq.ui.application.activity._common.screen.login.model.factory.FakeLoginViewModelFactory
import com.qubacy.geoqq.ui.application.activity._common.screen.login.model.factory._test.mock.LoginViewModelMockContext
import com.qubacy.geoqq.ui.application.activity._common.screen.login.model._common.state.LoginUiState
import dagger.Module
import dagger.Provides

@Module
object FakeLoginViewModelModule : FakeViewModelModule<LoginUiState, LoginViewModelMockContext>() {
    @Provides
    @LoginViewModelFactoryQualifier
    fun provideFakeLoginViewModelFactory(): ViewModelProvider.Factory {
        return FakeLoginViewModelFactory(mockContext)
    }
}