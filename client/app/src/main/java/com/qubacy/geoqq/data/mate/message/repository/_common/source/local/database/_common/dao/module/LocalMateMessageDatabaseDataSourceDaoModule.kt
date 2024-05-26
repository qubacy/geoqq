package com.qubacy.geoqq.data.mate.message.repository._common.source.local.database._common.dao.module

import com.qubacy.geoqq.data._common.repository._common.source.local.database._common.Database
import com.qubacy.geoqq.data.mate.message.repository._common.source.local.database._common.dao.LocalMateMessageDatabaseDataSourceDao
import dagger.Module
import dagger.Provides

@Module
abstract class LocalMateMessageDatabaseDataSourceDaoModule {
    @Provides
    fun provideLocalMateMessageDatabaseDataSourceDao(
        database: Database
    ): LocalMateMessageDatabaseDataSourceDao {
        return database.mateMessageDao()
    }
}