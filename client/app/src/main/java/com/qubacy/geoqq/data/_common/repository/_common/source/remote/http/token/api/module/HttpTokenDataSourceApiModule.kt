package com.qubacy.geoqq.data._common.repository._common.source.remote.http.token.api.module

import com.qubacy.geoqq.data._common.repository._common.source.remote.http.api.HttpApi
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.token.api.HttpTokenDataSourceApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class HttpTokenDataSourceApiModule {
    @Provides
    fun provideHttpTokenDataSourceApi(
        httpApi: HttpApi
    ): HttpTokenDataSourceApi {
        return httpApi.tokenApi
    }
}