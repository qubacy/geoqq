package com.qubacy.geoqq.domain.mate.request.usecase._di.module

import com.qubacy.geoqq.domain.mate.request.usecase.impl.MateRequestUseCaseImpl
import com.qubacy.geoqq.domain.mate.request.usecase._common.MateRequestUseCase
import dagger.Binds
import dagger.Module

@Module
abstract class MateRequestUseCaseModule {
    @Binds
    abstract fun bindMateRequestUseCase(
        mateRequestUseCase: MateRequestUseCaseImpl
    ): MateRequestUseCase
}