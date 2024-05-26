package com.qubacy.geoqq.data.mate.message.repository._di.module

import com.qubacy.geoqq.data.mate.message.repository._common.MateMessageDataRepository
import com.qubacy.geoqq.data.mate.message.repository.impl.MateMessageDataRepositoryImpl
import dagger.Binds
import dagger.Module

@Module
abstract class MateMessageDataRepositoryModule {
    @Binds
    abstract fun bindMateMessageDataRepository(
        mateMessageDataRepository: MateMessageDataRepositoryImpl
    ): MateMessageDataRepository
}