package com.qubacy.geoqq.data.auth.repository._common.source.remote.http.rest._common.api._di.module

import com.qubacy.geoqq.data._common.repository._common.source.remote.http.rest.api.HttpRestApi
import com.qubacy.geoqq.data.auth.repository._common.source.remote.http.rest._common.api.RemoteAuthHttpRestDataSourceApi
import dagger.Module
import dagger.Provides

@Module
abstract class RemoteAuthHttpRestDataSourceApiModule {
    companion object {
        @JvmStatic
        @Provides
        fun provideRemoteAuthHttpRestDataSourceApi(
            httpRestApi: HttpRestApi
        ): RemoteAuthHttpRestDataSourceApi {
            return httpRestApi.authApi
        }
    }
}