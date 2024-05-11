package com.qubacy.geoqq.data.auth.repository.module

import com.qubacy.geoqq.data._common.repository._common.source.local.database.error._common.LocalErrorDatabaseDataSource
import com.qubacy.geoqq.data._common.repository._common.source.local.datastore.token._common.LocalTokenDataStoreDataSource
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.rest.token._common.RemoteTokenHttpRestDataSource
import com.qubacy.geoqq.data.auth.repository._common.AuthDataRepository
import com.qubacy.geoqq.data.auth.repository.impl.AuthDataRepositoryImpl
import com.qubacy.geoqq.data.auth.repository._common.source.local.database._common.LocalAuthDatabaseDataSource
import com.qubacy.geoqq.data.auth.repository._common.source.remote.http.rest._common.RemoteAuthHttpRestDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object AuthDataRepositoryModule {
    @Provides
    fun provideAuthDataRepository(
        localErrorDataSource: LocalErrorDatabaseDataSource,
        localAuthDatabaseDatabaseDataSource: LocalAuthDatabaseDataSource,
        localTokenDataStoreDataSource: LocalTokenDataStoreDataSource,
        remoteAuthHttpRestDataSource: RemoteAuthHttpRestDataSource,
        remoteTokenHttpRestDataSource: RemoteTokenHttpRestDataSource
    ): AuthDataRepository {
        return AuthDataRepositoryImpl(
            localErrorDataSource,
            localTokenDataStoreDataSource,
            localAuthDatabaseDatabaseDataSource,
            remoteTokenHttpRestDataSource,
            remoteAuthHttpRestDataSource
        )
    }
}