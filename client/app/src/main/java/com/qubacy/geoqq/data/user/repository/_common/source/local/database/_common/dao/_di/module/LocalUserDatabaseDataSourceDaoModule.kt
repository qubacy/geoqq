package com.qubacy.geoqq.data.user.repository._common.source.local.database._common.dao._di.module

import com.qubacy.geoqq.data._common.repository._common.source.local.database._common.Database
import com.qubacy.geoqq.data.user.repository._common.source.local.database._common.dao.LocalUserDatabaseDataSourceDao
import dagger.Module
import dagger.Provides

@Module
abstract class LocalUserDatabaseDataSourceDaoModule {
    companion object {
        @JvmStatic
        @Provides
        fun provideLocalUserDatabaseDataSourceDao(
            database: Database
        ): LocalUserDatabaseDataSourceDao {
            return database.userDao()
        }
    }
}