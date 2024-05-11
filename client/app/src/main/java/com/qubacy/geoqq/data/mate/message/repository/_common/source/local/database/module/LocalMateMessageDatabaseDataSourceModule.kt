package com.qubacy.geoqq.data.mate.message.repository._common.source.local.database.module

import com.qubacy.geoqq.data.mate.message.repository._common.source.local.database._common.LocalMateMessageDatabaseDataSource
import com.qubacy.geoqq.data.mate.message.repository._common.source.local.database._common.dao.LocalMateMessageDatabaseDataSourceDao
import com.qubacy.geoqq.data.mate.message.repository._common.source.local.database.impl.LocalMateMessageDatabaseDataSourceImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object LocalMateMessageDatabaseDataSourceModule {
    @Provides
    fun provideLocalMateMessageDatabaseDataSource(
        localMateMessageDatabaseDataSourceDao: LocalMateMessageDatabaseDataSourceDao
    ): LocalMateMessageDatabaseDataSource {
        return LocalMateMessageDatabaseDataSourceImpl(localMateMessageDatabaseDataSourceDao)
    }
}