package com.qubacy.geoqq.data.mate.message.repository.source.http.api.module

import com.qubacy.geoqq.data._common.repository._common.source.remote.http.api.HttpApi
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
        httpApi: HttpApi
    ): HttpMateMessageDataSourceApi {
        return httpApi.mateMessageApi
    }
}