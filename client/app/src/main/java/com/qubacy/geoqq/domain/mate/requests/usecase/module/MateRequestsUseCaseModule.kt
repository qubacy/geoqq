package com.qubacy.geoqq.domain.mate.requests.usecase.module

import com.qubacy.geoqq.domain.mate.requests.usecase._common.MateRequestsUseCase
import com.qubacy.geoqq.domain.mate.requests.usecase.impl.MateRequestsUseCaseImpl
import dagger.Binds
import dagger.Module

@Module
abstract class MateRequestsUseCaseModule {
    @Binds
    abstract fun bindMateRequestsUseCase(
        mateRequestsUseCase: MateRequestsUseCaseImpl
    ): MateRequestsUseCase
}