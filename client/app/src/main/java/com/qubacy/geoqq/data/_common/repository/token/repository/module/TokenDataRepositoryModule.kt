package com.qubacy.geoqq.data._common.repository.token.repository.module

import com.qubacy.geoqq.data._common.repository.token.repository._common.TokenDataRepository
import com.qubacy.geoqq.data._common.repository.token.repository.impl.TokenDataRepositoryImpl
import dagger.Binds
import dagger.Module

@Module
abstract class TokenDataRepositoryModule {
    @Binds
    abstract fun bindTokenDataRepository(
        tokenDataRepository: TokenDataRepositoryImpl
    ): TokenDataRepository
}