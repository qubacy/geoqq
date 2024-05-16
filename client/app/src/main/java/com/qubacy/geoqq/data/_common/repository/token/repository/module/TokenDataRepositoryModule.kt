package com.qubacy.geoqq.data._common.repository.token.repository.module

import com.qubacy.geoqq.data._common.repository._common.source.local.database.error._common.LocalErrorDatabaseDataSource
import com.qubacy.geoqq.data._common.repository.token.repository._common.TokenDataRepository
import com.qubacy.geoqq.data._common.repository.token.repository._common.source.local.datastore._common.LocalTokenDataStoreDataSource
import com.qubacy.geoqq.data._common.repository.token.repository._common.source.remote.http.rest._common.RemoteTokenHttpRestDataSource
import com.qubacy.geoqq.data._common.repository.token.repository.impl.TokenDataRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object TokenDataRepositoryModule {
    @Provides
    fun provideTokenDataRepository(
        localErrorDatabaseDataSource: LocalErrorDatabaseDataSource,
        localTokenDataStoreDataSource: LocalTokenDataStoreDataSource,
        remoteTokenHttpRestDataSource: RemoteTokenHttpRestDataSource
    ): TokenDataRepository {
        return TokenDataRepositoryImpl(
            localErrorDatabaseDataSource,
            localTokenDataStoreDataSource,
            remoteTokenHttpRestDataSource
        )
    }
}