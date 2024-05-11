package com.qubacy.geoqq.domain.logout.usecase.module

import com.qubacy.geoqq.data._common.repository._common.source.local.database.error._common.LocalErrorDatabaseDataSource
import com.qubacy.geoqq.data.auth.repository._common.AuthDataRepository
import com.qubacy.geoqq.domain.logout.usecase._common.LogoutUseCase
import com.qubacy.geoqq.domain.logout.usecase.impl.LogoutUseCaseImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object LogoutUseCaseModule {
    @Provides
    fun provideLogoutUseCase(
        localErrorDataSource: LocalErrorDatabaseDataSource,
        tokenDataRepository: AuthDataRepository
    ): LogoutUseCase {
        return LogoutUseCaseImpl(localErrorDataSource, tokenDataRepository)
    }
}