package com.qubacy.geoqq.data.auth.repository.source.http.api.module

import com.qubacy.geoqq.data._common.repository._common.source.remote.http.rest.api.HttpRestApi
import com.qubacy.geoqq.data.auth.repository.source.http.api.HttpAuthDataSourceApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object HttpTokenDataSourceModule {
    @Provides
    fun provideHttpTokenDataSource(
        httpRestApi: HttpRestApi
    ): HttpAuthDataSourceApi {
        return httpRestApi.authApi
    }
}