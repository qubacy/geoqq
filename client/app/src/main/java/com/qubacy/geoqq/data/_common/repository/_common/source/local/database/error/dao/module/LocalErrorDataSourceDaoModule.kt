package com.qubacy.geoqq.data._common.repository._common.source.local.database.error.dao.module

import com.qubacy.geoqq.data._common.repository._common.source.local.database._common.Database
import com.qubacy.geoqq.data._common.repository._common.source.local.database.error.dao.LocalErrorDataSourceDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object LocalErrorDataSourceDaoModule {
    @Provides
    fun provideLocalErrorDataSourceDao(
        database: Database
    ): LocalErrorDataSourceDao {
        return database.errorDao()
    }
}