package com.qubacy.geoqq.domain.mate.requests.usecase.module

import com.qubacy.geoqq.data._common.repository._common.source.local.database.error.impl.LocalErrorDatabaseDataSourceImpl
import com.qubacy.geoqq.data.mate.request.repository.impl.MateRequestDataRepositoryImpl
import com.qubacy.geoqq.domain.interlocutor.usecase.InterlocutorUseCase
import com.qubacy.geoqq.domain.logout.usecase.LogoutUseCase
import com.qubacy.geoqq.domain.mate.request.usecase.MateRequestUseCase
import com.qubacy.geoqq.domain.mate.requests.usecase.MateRequestsUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object MateRequestsUseCaseModule {
    @Provides
    fun provideMateRequestsUseCase(
        localErrorDataSource: LocalErrorDatabaseDataSourceImpl,
        mateRequestUseCase: MateRequestUseCase,
        interlocutorUseCase: InterlocutorUseCase,
        logoutUseCase: LogoutUseCase,
        mateRequestDataRepository: MateRequestDataRepositoryImpl
    ): MateRequestsUseCase {
        return MateRequestsUseCase(
            localErrorDataSource, mateRequestUseCase,
            interlocutorUseCase, logoutUseCase, mateRequestDataRepository
        )
    }
}