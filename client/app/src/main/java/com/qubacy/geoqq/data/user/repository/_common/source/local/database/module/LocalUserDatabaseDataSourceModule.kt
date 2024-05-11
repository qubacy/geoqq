package com.qubacy.geoqq.data.user.repository._common.source.local.database.module

import com.qubacy.geoqq.data.user.repository._common.source.local.database._common.LocalUserDatabaseDataSource
import com.qubacy.geoqq.data.user.repository._common.source.local.database._common.dao.LocalUserDatabaseDataSourceDao
import com.qubacy.geoqq.data.user.repository._common.source.local.database.impl.LocalUserDatabaseDataSourceImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object LocalUserDatabaseDataSourceModule {
    @Provides
    fun provideLocalUserDatabaseDataSource(
        localUserDatabaseDataSourceDao: LocalUserDatabaseDataSourceDao
    ): LocalUserDatabaseDataSource {
        return LocalUserDatabaseDataSourceImpl(localUserDatabaseDataSourceDao)
    }
}