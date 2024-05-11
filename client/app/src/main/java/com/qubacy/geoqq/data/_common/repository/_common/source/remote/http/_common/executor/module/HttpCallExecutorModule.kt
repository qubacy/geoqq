package com.qubacy.geoqq.data._common.repository._common.source.remote.http._common.executor.module

import com.qubacy.geoqq.data._common.repository._common.source.local.database.error.impl.LocalErrorDatabaseDataSourceImpl
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
        errorSource: LocalErrorDatabaseDataSourceImpl,
        errorJsonAdapter: ErrorJsonAdapter
    ): HttpCallExecutor {
        return HttpCallExecutor(errorSource, errorJsonAdapter)
    }
}