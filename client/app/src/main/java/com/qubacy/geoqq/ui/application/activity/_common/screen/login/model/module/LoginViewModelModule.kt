package com.qubacy.geoqq.ui.application.activity._common.screen.login.model.module

import androidx.lifecycle.ViewModelProvider
import com.qubacy.geoqq.data._common.repository._common.source.local.database.error.impl.LocalErrorDatabaseDataSourceImpl
import com.qubacy.geoqq.domain.login.usecase.LoginUseCase
import com.qubacy.geoqq.ui.application.activity._common.screen.login.model.LoginViewModelFactory
import com.qubacy.geoqq.ui.application.activity._common.screen.login.model.LoginViewModelFactoryQualifier
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent

@Module
@InstallIn(ActivityRetainedComponent::class)
object LoginViewModelModule {
    @Provides
    @LoginViewModelFactoryQualifier
    fun provideLoginViewModelFactory(
        localErrorDataSource: LocalErrorDatabaseDataSourceImpl,
        loginUseCase: LoginUseCase
    ): ViewModelProvider.Factory {
        return LoginViewModelFactory(localErrorDataSource, loginUseCase)
    }
}