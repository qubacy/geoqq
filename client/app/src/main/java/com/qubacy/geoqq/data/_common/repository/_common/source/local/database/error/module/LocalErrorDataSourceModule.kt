package com.qubacy.geoqq.data._common.repository._common.source.local.database.error.module

import com.qubacy.geoqq.data._common.repository._common.source.local.database.error.LocalErrorDataSource
import com.qubacy.geoqq.data._common.repository._common.source.local.database.error.dao.LocalErrorDataSourceDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object LocalErrorDataSourceModule {
    @Provides
    fun provideLocalErrorDataSourceModule(
        localErrorDataSourceDao: LocalErrorDataSourceDao
    ): LocalErrorDataSource {
        return LocalErrorDataSource(localErrorDataSourceDao)
    }
}