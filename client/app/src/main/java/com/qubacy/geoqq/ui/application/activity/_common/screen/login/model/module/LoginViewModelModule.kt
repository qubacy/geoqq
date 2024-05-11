package com.qubacy.geoqq.ui.application.activity._common.screen.login.model.module

import androidx.lifecycle.ViewModelProvider
import com.qubacy.geoqq.data._common.repository._common.source.local.database.error._common.LocalErrorDatabaseDataSource
import com.qubacy.geoqq.domain.login.usecase._common.LoginUseCase
import com.qubacy.geoqq.ui.application.activity._common.screen.login.model.impl.LoginViewModelFactory
import com.qubacy.geoqq.ui.application.activity._common.screen.login.model.impl.LoginViewModelFactoryQualifier
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
        localErrorDataSource: LocalErrorDatabaseDataSource,
        loginUseCase: LoginUseCase
    ): ViewModelProvider.Factory {
        return LoginViewModelFactory(localErrorDataSource, loginUseCase)
    }
}