package com.qubacy.geoqq.domain.mate.requests.usecase.module

import com.qubacy.geoqq.data.error.repository.ErrorDataRepository
import com.qubacy.geoqq.data.mate.request.repository.MateRequestDataRepository
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
        errorDataRepository: ErrorDataRepository,
        mateRequestUseCase: MateRequestUseCase,
        interlocutorUseCase: InterlocutorUseCase,
        logoutUseCase: LogoutUseCase,
        mateRequestDataRepository: MateRequestDataRepository
    ): MateRequestsUseCase {
        return MateRequestsUseCase(
            errorDataRepository, mateRequestUseCase,
            interlocutorUseCase, logoutUseCase, mateRequestDataRepository
        )
    }
}