package com.qubacy.geoqq.data.mate.request.repository.module

import com.qubacy.geoqq.data.mate.request.repository._common.MateRequestDataRepository
import com.qubacy.geoqq.data.mate.request.repository.impl.MateRequestDataRepositoryImpl
import dagger.Binds
import dagger.Module

@Module
abstract class MateRequestDataRepositoryModule {
    @Binds
    abstract fun bindMateRequestDataRepository(
        mateRequestDataRepository: MateRequestDataRepositoryImpl
    ): MateRequestDataRepository
}