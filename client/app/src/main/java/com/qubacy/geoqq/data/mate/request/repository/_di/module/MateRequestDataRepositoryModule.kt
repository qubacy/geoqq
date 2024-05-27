package com.qubacy.geoqq.data.mate.request.repository._di.module

import com.qubacy.geoqq.data.mate.request.repository._common.MateRequestDataRepository
import com.qubacy.geoqq.data.mate.request.repository.impl.MateRequestDataRepositoryImpl
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module
abstract class MateRequestDataRepositoryModule {
    @Singleton
    @Binds
    abstract fun bindMateRequestDataRepository(
        mateRequestDataRepository: MateRequestDataRepositoryImpl
    ): MateRequestDataRepository
}