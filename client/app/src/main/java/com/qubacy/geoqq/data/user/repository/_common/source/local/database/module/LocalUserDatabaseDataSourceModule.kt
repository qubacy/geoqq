package com.qubacy.geoqq.data.user.repository._common.source.local.database.module

import com.qubacy.geoqq.data.user.repository._common.source.local.database._common.LocalUserDatabaseDataSource
import com.qubacy.geoqq.data.user.repository._common.source.local.database.impl.LocalUserDatabaseDataSourceImpl
import dagger.Binds
import dagger.Module

@Module
abstract class LocalUserDatabaseDataSourceModule {
    @Binds
    abstract fun provideLocalUserDatabaseDataSource(
        localUserDatabaseDataSource: LocalUserDatabaseDataSourceImpl
    ): LocalUserDatabaseDataSource
}