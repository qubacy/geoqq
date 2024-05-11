package com.qubacy.geoqq.data.image.repository._common.source.remote.http.rest._common.api.module

import com.qubacy.geoqq.data._common.repository._common.source.remote.http.rest.api.HttpRestApi
import com.qubacy.geoqq.data.image.repository._common.source.remote.http.rest._common.api.RemoteImageHttpRestDataSourceApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object RemoteImageHttpRestDataSourceModuleApi {
    @Provides
    fun provideRemoteImageHttpRestDataSourceApi(
        httpRestApi: HttpRestApi
    ): RemoteImageHttpRestDataSourceApi {
        return httpRestApi.imageApi
    }
}