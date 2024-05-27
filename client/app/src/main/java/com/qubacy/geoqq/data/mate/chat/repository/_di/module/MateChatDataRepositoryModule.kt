package com.qubacy.geoqq.data.mate.chat.repository._di.module

import com.qubacy.geoqq.data.mate.chat.repository._common.MateChatDataRepository
import com.qubacy.geoqq.data.mate.chat.repository.impl.MateChatDataRepositoryImpl
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module
abstract class MateChatDataRepositoryModule {
    @Singleton
    @Binds
    abstract fun bindMateChatDataRepository(
        mateChatDataRepository: MateChatDataRepositoryImpl
    ): MateChatDataRepository
}