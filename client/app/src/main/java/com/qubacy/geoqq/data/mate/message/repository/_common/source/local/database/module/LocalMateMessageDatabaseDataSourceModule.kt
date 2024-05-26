package com.qubacy.geoqq.data.mate.message.repository._common.source.local.database.module

import com.qubacy.geoqq.data.mate.message.repository._common.source.local.database._common.LocalMateMessageDatabaseDataSource
import com.qubacy.geoqq.data.mate.message.repository._common.source.local.database.impl.LocalMateMessageDatabaseDataSourceImpl
import dagger.Binds
import dagger.Module

@Module
abstract class LocalMateMessageDatabaseDataSourceModule {
    @Binds
    abstract fun bindLocalMateMessageDatabaseDataSource(
        localMateMessageDatabaseDataSource: LocalMateMessageDatabaseDataSourceImpl
    ): LocalMateMessageDatabaseDataSource
}