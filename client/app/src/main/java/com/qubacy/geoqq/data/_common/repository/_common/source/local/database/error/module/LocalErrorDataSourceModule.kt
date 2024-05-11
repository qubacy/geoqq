package com.qubacy.geoqq.data._common.repository._common.source.local.database.error.module

import com.qubacy.geoqq.data._common.repository._common.source.local.database.error.impl.LocalErrorDatabaseDataSourceImpl
import com.qubacy.geoqq.data._common.repository._common.source.local.database.error._common.LocalErrorDatabaseDataSource
import com.qubacy.geoqq.data._common.repository._common.source.local.database.error._common.dao.LocalErrorDataSourceDao
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
    ): LocalErrorDatabaseDataSource {
        return LocalErrorDatabaseDataSourceImpl(localErrorDataSourceDao)
    }
}