package com.qubacy.geoqq.domain.mate.request.usecase.module

import com.qubacy.geoqq.data._common.repository._common.source.local.database.error._common.LocalErrorDatabaseDataSource
import com.qubacy.geoqq.data.mate.request.repository._common.MateRequestDataRepository
import com.qubacy.geoqq.domain.logout.usecase._common.LogoutUseCase
import com.qubacy.geoqq.domain.mate.request.usecase.impl.MateRequestUseCaseImpl
import com.qubacy.geoqq.domain.mate.request.usecase._common.MateRequestUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object MateRequestUseCaseModule {
    @Provides
    fun provideMateRequestUseCase(
        localErrorDataSource: LocalErrorDatabaseDataSource,
        logoutUseCase: LogoutUseCase,
        mateRequestDataRepository: MateRequestDataRepository
    ): MateRequestUseCase {
        return MateRequestUseCaseImpl(
            localErrorDataSource, logoutUseCase, mateRequestDataRepository)
    }
}