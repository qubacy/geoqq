package com.qubacy.geoqq.ui.application.activity._common.screen.login.model.module

import androidx.lifecycle.ViewModelProvider
import com.qubacy.geoqq.ui.application.activity._common.screen.login.model.LoginViewModelFactoryQualifier
import com.qubacy.geoqq.ui.application.activity._common.screen.login.model.factory.FakeLoginViewModelFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.testing.TestInstallIn

@Module
@TestInstallIn(
    components = [ActivityRetainedComponent::class],
    replaces = [LoginViewModelModule::class]
)
object FakeLoginViewModelModule {
    @Provides
    @LoginViewModelFactoryQualifier
    fun provideFakeLoginViewModelFactory(): ViewModelProvider.Factory {
        return FakeLoginViewModelFactory()
    }
}