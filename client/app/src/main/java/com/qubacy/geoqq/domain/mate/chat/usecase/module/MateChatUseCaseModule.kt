package com.qubacy.geoqq.domain.mate.chat.usecase.module

import com.qubacy.geoqq.domain.mate.chat.usecase._common.MateChatUseCase
import com.qubacy.geoqq.domain.mate.chat.usecase.impl.MateChatUseCaseImpl
import dagger.Binds
import dagger.Module

@Module
abstract class MateChatUseCaseModule {
    @Binds
    abstract fun bindMateChatUseCase(mateChatUseCase: MateChatUseCaseImpl): MateChatUseCase
}