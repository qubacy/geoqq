package com.qubacy.geoqq.data._common.repository._common.source.local.database.error._common.dao.module

import com.qubacy.geoqq.data._common.repository._common.source.local.database._common.Database
import com.qubacy.geoqq.data._common.repository._common.source.local.database.error._common.dao.LocalErrorDataSourceDao
import dagger.Module
import dagger.Provides

@Module
abstract class LocalErrorDataSourceDaoModule {
    @Provides
    fun provideLocalErrorDataSourceDao(
        database: Database
    ): LocalErrorDataSourceDao {
        return database.errorDao()
    }
}