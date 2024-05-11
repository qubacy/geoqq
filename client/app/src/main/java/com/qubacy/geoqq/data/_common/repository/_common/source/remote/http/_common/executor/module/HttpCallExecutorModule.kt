package com.qubacy.geoqq.data._common.repository._common.source.remote.http._common.executor.module

import com.qubacy.geoqq.data._common.repository._common.source.local.database.error._common.LocalErrorDatabaseDataSource
import com.qubacy.geoqq.data._common.repository._common.source.remote.http._common.executor.HttpCallExecutor
import com.qubacy.geoqq.data._common.repository._common.source.remote.http._common.response.error.json.adapter.ErrorJsonAdapter
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object HttpCallExecutorModule {
    @Provides
    fun provideHttpCallExecutor(
        errorSource: LocalErrorDatabaseDataSource,
        errorJsonAdapter: ErrorJsonAdapter
    ): HttpCallExecutor {
        return HttpCallExecutor(errorSource, errorJsonAdapter)
    }
}