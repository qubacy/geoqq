package com.qubacy.geoqq.domain.mate.request.usecase.module

import com.qubacy.geoqq.data._common.repository._common.source.local.database.error.impl.LocalErrorDatabaseDataSourceImpl
import com.qubacy.geoqq.data.mate.request.repository.impl.MateRequestDataRepositoryImpl
import com.qubacy.geoqq.domain.logout.usecase.LogoutUseCase
import com.qubacy.geoqq.domain.mate.request.usecase.MateRequestUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object MateRequestUseCaseModule {
    @Provides
    fun provideMateRequestUseCase(
        localErrorDataSource: LocalErrorDatabaseDataSourceImpl,
        logoutUseCase: LogoutUseCase,
        mateRequestDataRepository: MateRequestDataRepositoryImpl
    ): MateRequestUseCase {
        return MateRequestUseCase(localErrorDataSource, logoutUseCase, mateRequestDataRepository)
    }
}