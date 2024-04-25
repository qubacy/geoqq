package com.qubacy.geoqq.data.mate.message.repository.source.local.module

import com.qubacy.geoqq.data._common.repository._common.source.local.database.Database
import com.qubacy.geoqq.data.mate.message.repository.source.local.LocalMateMessageDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object LocalMateMessageDataSourceModule {
    @Provides
    fun provideLocalMateMessageDataSource(
        database: Database
    ): LocalMateMessageDataSource {
        return database.mateMessageDao()
    }
}