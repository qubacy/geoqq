package com.qubacy.geoqq.data.user.repository._common.source.local.database._common.dao.module

import com.qubacy.geoqq.data._common.repository._common.source.local.database._common.Database
import com.qubacy.geoqq.data.user.repository._common.source.local.database._common.dao.LocalUserDatabaseDataSourceDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class LocalUserDatabaseDataSourceDaoModule {
    @Provides
    fun provideLocalUserDatabaseDataSourceDao(
        database: Database
    ): LocalUserDatabaseDataSourceDao {
        return database.userDao()
    }
}