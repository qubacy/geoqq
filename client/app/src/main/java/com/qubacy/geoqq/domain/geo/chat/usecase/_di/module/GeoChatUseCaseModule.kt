package com.qubacy.geoqq.domain.geo.chat.usecase._di.module

import com.qubacy.geoqq.domain.geo.chat.usecase.impl.GeoChatUseCaseImpl
import com.qubacy.geoqq.domain.geo.chat.usecase._common.GeoChatUseCase
import dagger.Binds
import dagger.Module

@Module
abstract class GeoChatUseCaseModule {
    @Binds
    abstract fun bindGeoChatUseCase(geoChatUseCase: GeoChatUseCaseImpl): GeoChatUseCase
}