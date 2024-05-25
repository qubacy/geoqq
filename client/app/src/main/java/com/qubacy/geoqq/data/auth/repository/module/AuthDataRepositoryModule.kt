package com.qubacy.geoqq.data.auth.repository.module

import com.qubacy.geoqq.data.auth.repository._common.AuthDataRepository
import com.qubacy.geoqq.data.auth.repository.impl.AuthDataRepositoryImpl
import dagger.Binds
import dagger.Module

@Module
abstract class AuthDataRepositoryModule {
    @Binds
    abstract fun bindAuthDataRepository(
        authDataRepository: AuthDataRepositoryImpl
    ): AuthDataRepository
}