package com.qubacy.geoqq.domain.login.usecase.module

import com.qubacy.geoqq.data._common.repository._common.source.local.database.error._common.LocalErrorDatabaseDataSource
import com.qubacy.geoqq.data.auth.repository._common.AuthDataRepository
import com.qubacy.geoqq.domain.login.usecase._common.LoginUseCase
import com.qubacy.geoqq.domain.login.usecase.impl.LoginUseCaseImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object LoginUseCaseModule {
    @Provides
    fun provideLoginUseCase(
        localErrorDataSource: LocalErrorDatabaseDataSource,
        tokenDataRepository: AuthDataRepository
    ): LoginUseCase {
        return LoginUseCaseImpl(localErrorDataSource, tokenDataRepository)
    }
}