package com.qubacy.geoqq.domain.login.usecase.module

import com.qubacy.geoqq.data.error.repository.ErrorDataRepository
import com.qubacy.geoqq.data.token.repository.TokenDataRepository
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
        errorDataRepository: ErrorDataRepository,
        tokenDataRepository: TokenDataRepository
    ): LoginUseCase {
        return LoginUseCase(errorDataRepository, tokenDataRepository)
    }
}