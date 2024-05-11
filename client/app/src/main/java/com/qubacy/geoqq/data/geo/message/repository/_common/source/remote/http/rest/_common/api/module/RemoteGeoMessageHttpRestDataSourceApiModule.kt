package com.qubacy.geoqq.data.geo.message.repository._common.source.remote.http.rest._common.api.module

import com.qubacy.geoqq.data._common.repository._common.source.remote.http.rest.api.HttpRestApi
import com.qubacy.geoqq.data.geo.message.repository._common.source.remote.http.rest._common.api.RemoteGeoMessageHttpRestDataSourceApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object RemoteGeoMessageHttpRestDataSourceApiModule {
    @Provides
    fun provideRemoteGeoMessageHttpRestDataSourceApi(
        httpRestApi: HttpRestApi
    ): RemoteGeoMessageHttpRestDataSourceApi {
        return httpRestApi.geoChatApi
    }
}