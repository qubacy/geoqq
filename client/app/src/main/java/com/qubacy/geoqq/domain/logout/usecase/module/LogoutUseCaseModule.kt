package com.qubacy.geoqq.domain.logout.usecase.module

import com.qubacy.geoqq.data._common.repository._common.source.local.database.error.LocalErrorDataSource
import com.qubacy.geoqq.data.auth.repository.AuthDataRepository
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
        localErrorDataSource: LocalErrorDataSource,
        tokenDataRepository: AuthDataRepository
    ): LogoutUseCase {
        return LogoutUseCase(localErrorDataSource, tokenDataRepository)
    }
}