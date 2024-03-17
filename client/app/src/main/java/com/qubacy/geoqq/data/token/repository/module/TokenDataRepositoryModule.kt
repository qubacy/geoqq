package com.qubacy.geoqq.data.token.repository.module

import com.qubacy.geoqq.data.error.repository.ErrorDataRepository
import com.qubacy.geoqq.data.token.repository.TokenDataRepository
import com.qubacy.geoqq.data.token.repository.source.http.HttpTokenDataSource
import com.qubacy.geoqq.data.token.repository.source.local.LocalTokenDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object TokenDataRepositoryModule {
    @Provides
    fun provideTokenDataRepository(
        errorDataRepository: ErrorDataRepository,
        localTokenDataSource: LocalTokenDataSource,
        httpTokenDataSource: HttpTokenDataSource
    ): TokenDataRepository {
        return TokenDataRepository(
            errorDataRepository,
            localTokenDataSource,
            httpTokenDataSource
        )
    }
}