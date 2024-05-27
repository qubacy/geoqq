package com.qubacy.geoqq.data.auth.repository._di.module

import com.qubacy.geoqq.data.auth.repository._common.AuthDataRepository
import com.qubacy.geoqq.data.auth.repository.impl.AuthDataRepositoryImpl
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module
abstract class AuthDataRepositoryModule {
    @Singleton
    @Binds
    abstract fun bindAuthDataRepository(
        authDataRepository: AuthDataRepositoryImpl
    ): AuthDataRepository
}