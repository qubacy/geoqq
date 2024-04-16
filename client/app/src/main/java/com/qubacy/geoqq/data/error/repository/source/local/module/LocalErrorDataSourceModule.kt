package com.qubacy.geoqq.data.error.repository.source.local.module

import com.qubacy.geoqq.data._common.repository._common.source.local._common.database.Database
import com.qubacy.geoqq.data.error.repository.source.local.LocalErrorDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object LocalErrorDataSourceModule {
    @Provides
    fun provideLocalErrorDataSource(
        database: Database
    ): LocalErrorDataSource {
        return database.errorDao()
    }
}