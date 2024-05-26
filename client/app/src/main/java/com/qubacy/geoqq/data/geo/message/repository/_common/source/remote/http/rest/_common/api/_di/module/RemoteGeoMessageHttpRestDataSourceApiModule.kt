package com.qubacy.geoqq.data.geo.message.repository._common.source.remote.http.rest._common.api._di.module

import com.qubacy.geoqq.data._common.repository._common.source.remote.http.rest.api.HttpRestApi
import com.qubacy.geoqq.data.geo.message.repository._common.source.remote.http.rest._common.api.RemoteGeoMessageHttpRestDataSourceApi
import dagger.Module
import dagger.Provides

@Module
abstract class RemoteGeoMessageHttpRestDataSourceApiModule {
    companion object {
        @JvmStatic
        @Provides
        fun provideRemoteGeoMessageHttpRestDataSourceApi(
            httpRestApi: HttpRestApi
        ): RemoteGeoMessageHttpRestDataSourceApi {
            return httpRestApi.geoChatApi
        }
    }
}