package com.qubacy.geoqq.ui.application.activity._common.screen.login.model._di.module

import androidx.lifecycle.ViewModelProvider
import com.qubacy.geoqq.ui.application.activity._common.screen.login.model.impl.LoginViewModelImplFactory
import com.qubacy.geoqq.ui.application.activity._common.screen.login.model.impl.LoginViewModelFactoryQualifier
import dagger.Binds
import dagger.Module

@Module
abstract class LoginViewModelModule {
    @Binds
    @LoginViewModelFactoryQualifier
    abstract fun bindLoginViewModelFactory(
        loginViewModelFactory: LoginViewModelImplFactory
    ): ViewModelProvider.Factory
}