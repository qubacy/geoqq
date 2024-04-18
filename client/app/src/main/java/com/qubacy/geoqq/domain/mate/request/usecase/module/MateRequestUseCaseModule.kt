package com.qubacy.geoqq.domain.mate.request.usecase.module

import com.qubacy.geoqq.data.error.repository.ErrorDataRepository
import com.qubacy.geoqq.data.mate.request.repository.MateRequestDataRepository
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
        errorDataRepository: ErrorDataRepository,
        mateRequestDataRepository: MateRequestDataRepository
    ): MateRequestUseCase {
        return MateRequestUseCase(errorDataRepository, mateRequestDataRepository)
    }
}