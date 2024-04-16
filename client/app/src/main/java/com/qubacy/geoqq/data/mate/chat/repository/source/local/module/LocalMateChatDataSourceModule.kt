package com.qubacy.geoqq.data.mate.chat.repository.source.local.module

import com.qubacy.geoqq.data._common.repository._common.source.local._common.database.Database
import com.qubacy.geoqq.data.mate.chat.repository.source.local.LocalMateChatDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object LocalMateChatDataSourceModule {
    @Provides
    fun provideLocalMateChatDataSource(
        database: Database
    ): LocalMateChatDataSource {
        return database.mateChatDao()
    }
}