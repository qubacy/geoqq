package com.qubacy.geoqq.domain.mate.chats.usecase.module

import com.qubacy.geoqq.domain.mate.chats.usecase._common.MateChatsUseCase
import com.qubacy.geoqq.domain.mate.chats.usecase.impl.MateChatsUseCaseImpl
import dagger.Binds
import dagger.Module

@Module
abstract class MateChatsUseCaseModule {
    @Binds
    abstract fun bindMateChatsUseCase(mateChatsUseCase: MateChatsUseCaseImpl): MateChatsUseCase
}