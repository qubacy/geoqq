package com.qubacy.geoqq.data.mate.request.repository.source.http.api.module

import com.qubacy.geoqq.data._common.repository._common.source.remote.http.rest.api.HttpRestApi
import com.qubacy.geoqq.data.mate.request.repository.source.http.api.HttpMateRequestDataSourceApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object HttpMateRequestDataSourceModuleApi {
    @Provides
    fun provideHttpMateRequestDataSourceApi(
        httpRestApi: HttpRestApi
    ): HttpMateRequestDataSourceApi {
        return httpRestApi.mateRequestApi
    }
}