package com.qubacy.geoqq.data.mate.chat.repository._common.source.local.database._di.module

import com.qubacy.geoqq.data.mate.chat.repository._common.source.local.database._common.LocalMateChatDatabaseDataSource
import com.qubacy.geoqq.data.mate.chat.repository._common.source.local.database.impl.LocalMateChatDatabaseDataSourceImpl
import dagger.Binds
import dagger.Module

@Module
abstract class LocalMateChatDatabaseDataSourceModule {
    @Binds
    abstract fun bindLocalMateChatDatabaseDataSource(
        localMateChatDatabaseDataSource: LocalMateChatDatabaseDataSourceImpl
    ): LocalMateChatDatabaseDataSource
}