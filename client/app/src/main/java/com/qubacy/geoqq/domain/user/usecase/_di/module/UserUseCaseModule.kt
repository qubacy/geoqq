package com.qubacy.geoqq.domain.user.usecase._di.module

import com.qubacy.geoqq.domain.user.usecase._common.UserUseCase
import com.qubacy.geoqq.domain.user.usecase.impl.UserUseCaseImpl
import dagger.Binds
import dagger.Module

@Module
abstract class UserUseCaseModule {
    @Binds
    abstract fun bindUserUseCase(userUseCase: UserUseCaseImpl): UserUseCase
}