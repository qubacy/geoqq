package com.qubacy.geoqq.data.auth.repository._common.source.local.database._di.module

import com.qubacy.geoqq.data._common.repository._common.source.local.database._common.Database
import com.qubacy.geoqq.data.auth.repository._common.source.local.database._common.LocalAuthDatabaseDataSource
import dagger.Module
import dagger.Provides

@Module
abstract class LocalAuthDatabaseDataSourceModule {
    companion object {
        @JvmStatic
        @Provides
        fun provideLocalAuthDatabaseDataSourceModule(
            database: Database
        ): LocalAuthDatabaseDataSource {
            return database.authDao()
        }
    }
}