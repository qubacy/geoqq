package com.qubacy.geoqq.data.mate.request.repository.source.http.api.module

import com.qubacy.geoqq.data._common.repository._common.source.remote.http.api.HttpApi
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
        httpApi: HttpApi
    ): HttpMateRequestDataSourceApi {
        return httpApi.mateRequestApi
    }
}