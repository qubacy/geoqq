package com.qubacy.geoqq.domain.login.usecase._di.module

import com.qubacy.geoqq.domain.login.usecase._common.LoginUseCase
import com.qubacy.geoqq.domain.login.usecase.impl.LoginUseCaseImpl
import dagger.Binds
import dagger.Module

@Module
abstract class LoginUseCaseModule {
    @Binds
    abstract fun bindLoginUseCase(loginUseCase: LoginUseCaseImpl): LoginUseCase
}