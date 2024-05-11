package com.qubacy.geoqq.domain.login.usecase.module

import com.qubacy.geoqq.data._common.repository._common.source.local.database.error.impl.LocalErrorDatabaseDataSourceImpl
import com.qubacy.geoqq.data.auth.repository.impl.AuthDataRepositoryImpl
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
        localErrorDataSource: LocalErrorDatabaseDataSourceImpl,
        tokenDataRepository: AuthDataRepositoryImpl
    ): LoginUseCase {
        return LoginUseCase(localErrorDataSource, tokenDataRepository)
    }
}