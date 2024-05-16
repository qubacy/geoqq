package com.qubacy.geoqq.domain.mate.requests.usecase.module

import com.qubacy.geoqq.data._common.repository._common.source.local.database.error._common.LocalErrorDatabaseDataSource
import com.qubacy.geoqq.data.mate.request.repository._common.MateRequestDataRepository
import com.qubacy.geoqq.domain.user.usecase._common.InterlocutorUseCase
import com.qubacy.geoqq.domain.logout.usecase._common.LogoutUseCase
import com.qubacy.geoqq.domain.mate.request.usecase._common.MateRequestUseCase
import com.qubacy.geoqq.domain.mate.requests.usecase._common.MateRequestsUseCase
import com.qubacy.geoqq.domain.mate.requests.usecase.impl.MateRequestsUseCaseImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object MateRequestsUseCaseModule {
    @Provides
    fun provideMateRequestsUseCase(
        localErrorDataSource: LocalErrorDatabaseDataSource,
        mateRequestUseCase: MateRequestUseCase,
        interlocutorUseCase: InterlocutorUseCase,
        logoutUseCase: LogoutUseCase,
        mateRequestDataRepository: MateRequestDataRepository
    ): MateRequestsUseCase {
        return MateRequestsUseCaseImpl(
            localErrorDataSource, mateRequestUseCase,
            interlocutorUseCase, logoutUseCase, mateRequestDataRepository
        )
    }
}