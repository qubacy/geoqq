package com.qubacy.geoqq.data._common.repository.token.repository._di.module

import com.qubacy.geoqq.data._common.repository.token.repository._common.TokenDataRepository
import com.qubacy.geoqq.data._common.repository.token.repository.impl.TokenDataRepositoryImpl
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module
abstract class TokenDataRepositoryModule {
    @Singleton
    @Binds
    abstract fun bindTokenDataRepository(
        tokenDataRepository: TokenDataRepositoryImpl
    ): TokenDataRepository
}