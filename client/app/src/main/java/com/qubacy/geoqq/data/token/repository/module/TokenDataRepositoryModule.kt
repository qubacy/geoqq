package com.qubacy.geoqq.data.token.repository.module

import com.qubacy.geoqq.data.error.repository.ErrorDataRepository
import com.qubacy.geoqq.data.token.repository.TokenDataRepository
import com.qubacy.geoqq.data.token.repository.source.http.HttpTokenDataSource
import com.qubacy.geoqq.data.token.repository.source.local.database.LocalDatabaseTokenDataSource
import com.qubacy.geoqq.data.token.repository.source.local.store.LocalStoreTokenDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient

@Module
@InstallIn(SingletonComponent::class)
object TokenDataRepositoryModule {
    @Provides
    fun provideTokenDataRepository(
        errorDataRepository: ErrorDataRepository,
        localStoreTokenDataSource: LocalStoreTokenDataSource,
        localDatabaseTokenDataSource: LocalDatabaseTokenDataSource,
        httpTokenDataSource: HttpTokenDataSource,
        httpClient: OkHttpClient
    ): TokenDataRepository {
        return TokenDataRepository(
            errorDataRepository,
            localStoreTokenDataSource,
            localDatabaseTokenDataSource,
            httpTokenDataSource,
            httpClient
        )
    }
}