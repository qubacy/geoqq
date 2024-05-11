package com.qubacy.geoqq.domain.logout.usecase.module

import com.qubacy.geoqq.data._common.repository._common.source.local.database.error.impl.LocalErrorDatabaseDataSourceImpl
import com.qubacy.geoqq.data.auth.repository.impl.AuthDataRepositoryImpl
import com.qubacy.geoqq.domain.logout.usecase.LogoutUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object LogoutUseCaseModule {
    @Provides
    fun provideLogoutUseCase(
        localErrorDataSource: LocalErrorDatabaseDataSourceImpl,
        tokenDataRepository: AuthDataRepositoryImpl
    ): LogoutUseCase {
        return LogoutUseCase(localErrorDataSource, tokenDataRepository)
    }
}