package com.qubacy.geoqq.data.mate.chat.repository._di.module

import com.qubacy.geoqq.data.mate.chat.repository._common.MateChatDataRepository
import com.qubacy.geoqq.data.mate.chat.repository.impl.MateChatDataRepositoryImpl
import dagger.Binds
import dagger.Module

@Module
abstract class MateChatDataRepositoryModule {
    @Binds
    abstract fun bindMateChatDataRepository(
        mateChatDataRepository: MateChatDataRepositoryImpl
    ): MateChatDataRepository
}