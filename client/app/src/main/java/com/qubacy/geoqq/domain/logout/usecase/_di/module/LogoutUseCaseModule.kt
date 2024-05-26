package com.qubacy.geoqq.domain.logout.usecase._di.module

import com.qubacy.geoqq.domain.logout.usecase._common.LogoutUseCase
import com.qubacy.geoqq.domain.logout.usecase.impl.LogoutUseCaseImpl
import dagger.Binds
import dagger.Module

@Module
abstract class LogoutUseCaseModule {
    @Binds
    abstract fun bindLogoutUseCase(logoutUseCase: LogoutUseCaseImpl): LogoutUseCase
}