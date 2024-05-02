package com.qubacy.geoqq.data._common.repository._common.source.remote.http.token.module

import com.qubacy.geoqq.data._common.repository._common.source.remote.http.api.HttpApi
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.executor.HttpCallExecutor
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.token.HttpTokenDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object HttpTokenDataSourceModule {
    @Provides
    fun provideHttpTokenDataSource(
        httpCallExecutor: HttpCallExecutor,
        httpApi: HttpApi
    ): HttpTokenDataSource {
        val tokenApi = httpApi.tokenApi

        return HttpTokenDataSource(httpCallExecutor).apply {
            setHttpTokenDataSourceApi(tokenApi)
        }
    }
}