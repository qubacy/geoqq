package com.qubacy.geoqq.data.auth.repository.source.local.database.module

import com.qubacy.geoqq.data._common.repository._common.source.local.database._common.Database
import com.qubacy.geoqq.data.auth.repository.source.local.database.LocalAuthDatabaseDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object LocalAuthDatabaseDataSourceModule {
    @Provides
    fun provideLocalAuthDatabaseDataSourceModule(
        database: Database
    ): LocalAuthDatabaseDataSource {
        return database.authDao()
    }
}