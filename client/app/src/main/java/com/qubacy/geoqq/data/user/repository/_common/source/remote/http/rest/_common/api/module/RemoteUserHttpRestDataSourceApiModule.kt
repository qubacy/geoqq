package com.qubacy.geoqq.data.user.repository._common.source.remote.http.rest._common.api.module

import com.qubacy.geoqq.data._common.repository._common.source.remote.http.rest.api.HttpRestApi
import com.qubacy.geoqq.data.user.repository._common.source.remote.http.rest._common.api.RemoteUserHttpRestDataSourceApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object RemoteUserHttpRestDataSourceApiModule {
    @Provides
    fun provideRemoteUserHttpRestDataSourceApi(
        httpRestApi: HttpRestApi
    ): RemoteUserHttpRestDataSourceApi {
        return httpRestApi.userApi
    }
}