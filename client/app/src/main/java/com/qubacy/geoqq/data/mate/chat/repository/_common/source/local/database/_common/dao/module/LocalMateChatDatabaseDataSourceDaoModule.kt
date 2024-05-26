package com.qubacy.geoqq.data.mate.chat.repository._common.source.local.database._common.dao.module

import com.qubacy.geoqq.data._common.repository._common.source.local.database._common.Database
import com.qubacy.geoqq.data.mate.chat.repository._common.source.local.database._common.dao.LocalMateChatDatabaseDataSourceDao
import dagger.Module
import dagger.Provides

@Module
abstract class LocalMateChatDatabaseDataSourceDaoModule {
    @Provides
    fun provideLocalMateChatDatabaseDataSourceDao(
        database: Database
    ): LocalMateChatDatabaseDataSourceDao {
        return database.mateChatDao()
    }
}