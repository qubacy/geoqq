package com.qubacy.geoqq.domain.login.usecase.module

import com.qubacy.geoqq.data._common.repository._common.source.local.database.error.LocalErrorDataSource
import com.qubacy.geoqq.data.auth.repository.AuthDataRepository
import com.qubacy.geoqq.domain.login.usecase.LoginUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object LoginUseCaseModule {
    @Provides
    fun provideLoginUseCase(
        localErrorDataSource: LocalErrorDataSource,
        tokenDataRepository: AuthDataRepository
    ): LoginUseCase {
        return LoginUseCase(localErrorDataSource, tokenDataRepository)
    }
}