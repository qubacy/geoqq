package com.qubacy.geoqq.data.image.repository._common.source.remote.http.rest._common.api._di.module

import com.qubacy.geoqq.data._common.repository._common.source.remote.http.rest.api.HttpRestApi
import com.qubacy.geoqq.data.image.repository._common.source.remote.http.rest._common.api.RemoteImageHttpRestDataSourceApi
import dagger.Module
import dagger.Provides

@Module
abstract class RemoteImageHttpRestDataSourceApiModule {
    companion object {
        @JvmStatic
        @Provides
        fun provideRemoteImageHttpRestDataSourceApi(
            httpRestApi: HttpRestApi
        ): RemoteImageHttpRestDataSourceApi {
            return httpRestApi.imageApi
        }
    }
}