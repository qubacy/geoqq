package com.qubacy.geoqq.domain.logout.usecase.module

import com.qubacy.geoqq.data.error.repository.ErrorDataRepository
import com.qubacy.geoqq.data.token.repository.TokenDataRepository
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
        errorDataRepository: ErrorDataRepository,
        tokenDataRepository: TokenDataRepository
    ): LogoutUseCase {
        return LogoutUseCase(errorDataRepository, tokenDataRepository)
    }
}