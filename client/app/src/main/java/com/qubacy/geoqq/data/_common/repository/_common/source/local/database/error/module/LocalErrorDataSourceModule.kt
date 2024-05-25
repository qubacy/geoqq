package com.qubacy.geoqq.data._common.repository._common.source.local.database.error.module

import com.qubacy.geoqq.data._common.repository._common.source.local.database.error._common.LocalErrorDatabaseDataSource
import com.qubacy.geoqq.data._common.repository._common.source.local.database.error.impl.LocalErrorDatabaseDataSourceImpl
import dagger.Binds
import dagger.Module

@Module
abstract class LocalErrorDataSourceModule {
    @Binds
    abstract fun bindLocalErrorDataSourceModule(
        localErrorDatabaseDataSource: LocalErrorDatabaseDataSourceImpl
    ): LocalErrorDatabaseDataSource
}