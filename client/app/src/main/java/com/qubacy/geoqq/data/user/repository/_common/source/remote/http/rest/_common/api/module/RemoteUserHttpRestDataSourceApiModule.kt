package com.qubacy.geoqq.data.user.repository._common.source.remote.http.rest._common.api.module

import com.qubacy.geoqq.data._common.repository._common.source.remote.http.rest.api.HttpRestApi
import com.qubacy.geoqq.data.user.repository._common.source.remote.http.rest._common.api.RemoteUserHttpRestDataSourceApi
import dagger.Module
import dagger.Provides

@Module
abstract class RemoteUserHttpRestDataSourceApiModule {
    @Provides
    fun provideRemoteUserHttpRestDataSourceApi(
        httpRestApi: HttpRestApi
    ): RemoteUserHttpRestDataSourceApi {
        return httpRestApi.userApi
    }
}