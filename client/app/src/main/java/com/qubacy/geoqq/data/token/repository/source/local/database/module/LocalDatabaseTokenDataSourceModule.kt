package com.qubacy.geoqq.data.token.repository.source.local.database.module

import com.qubacy.geoqq.data._common.repository._common.source.local._common.database.Database
import com.qubacy.geoqq.data.token.repository.source.local.database.LocalDatabaseTokenDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object LocalDatabaseTokenDataSourceModule {
    @Provides
    fun provideLocalDatabaseTokenDataSource(
        database: Database
    ): LocalDatabaseTokenDataSource {
        return database.tokenDao()
    }
}