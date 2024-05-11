package com.qubacy.geoqq.data.mate.chat.repository._common.source.local.database.module

import com.qubacy.geoqq.data.mate.chat.repository._common.source.local.database._common.LocalMateChatDatabaseDataSource
import com.qubacy.geoqq.data.mate.chat.repository._common.source.local.database._common.dao.LocalMateChatDatabaseDataSourceDao
import com.qubacy.geoqq.data.mate.chat.repository._common.source.local.database.impl.LocalMateChatDatabaseDataSourceImpl
import com.qubacy.geoqq.data.mate.message.repository._common.source.local.database._common.LocalMateMessageDatabaseDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object LocalMateChatDatabaseDataSourceModule {
    @Provides
    fun provideLocalMateChatDatabaseDataSource(
        localMateChatDatabaseDataSourceDao: LocalMateChatDatabaseDataSourceDao,
        localMateMessageDatabaseDataSource: LocalMateMessageDatabaseDataSource
    ): LocalMateChatDatabaseDataSource {
        return LocalMateChatDatabaseDataSourceImpl(
            localMateChatDatabaseDataSourceDao,
            localMateMessageDatabaseDataSource
        )
    }
}