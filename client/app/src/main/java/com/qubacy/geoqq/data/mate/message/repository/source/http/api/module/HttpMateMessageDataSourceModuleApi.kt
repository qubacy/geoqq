package com.qubacy.geoqq.data.mate.message.repository.source.http.api.module

import com.qubacy.geoqq.data._common.repository._common.source.remote.http.rest.api.HttpRestApi
import com.qubacy.geoqq.data.mate.message.repository.source.http.api.HttpMateMessageDataSourceApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class HttpMateMessageDataSourceModuleApi {
    @Provides
    fun provideHttpMateMessageDataSourceModule(
        httpRestApi: HttpRestApi
    ): HttpMateMessageDataSourceApi {
        return httpRestApi.mateMessageApi
    }
}