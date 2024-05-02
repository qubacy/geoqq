package com.qubacy.geoqq.data.auth.repository.module

import com.qubacy.geoqq.data._common.repository._common.source.local.database.error.LocalErrorDataSource
import com.qubacy.geoqq.data._common.repository._common.source.local.datastore.token.LocalTokenDataStoreDataSource
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.token.HttpTokenDataSource
import com.qubacy.geoqq.data.auth.repository.AuthDataRepository
import com.qubacy.geoqq.data.auth.repository.source.http.HttpAuthDataSource
import com.qubacy.geoqq.data.auth.repository.source.local.database.LocalAuthDatabaseDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object AuthDataRepositoryModule {
    @Provides
    fun provideTokenDataRepository(
        localErrorDataSource: LocalErrorDataSource,
        localAuthDatabaseDatabaseDataSource: LocalAuthDatabaseDataSource,
        localTokenDataStoreDataSource: LocalTokenDataStoreDataSource,
        httpAuthDataSource: HttpAuthDataSource,
        httpTokenDataSource: HttpTokenDataSource
    ): AuthDataRepository {
        return AuthDataRepository(
            localErrorDataSource,
            localTokenDataStoreDataSource,
            localAuthDatabaseDatabaseDataSource,
            httpTokenDataSource,
            httpAuthDataSource
        )
    }
}