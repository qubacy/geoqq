package com.qubacy.geoqq.data.myprofile.repository._common.source.remote.http.rest._common.api.module

import com.qubacy.geoqq.data._common.repository._common.source.remote.http.rest.api.HttpRestApi
import com.qubacy.geoqq.data.myprofile.repository._common.source.remote.http.rest._common.api.RemoteMyProfileHttpRestDataSourceApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object RemoteMyProfileHttpRestDataSourceModuleApi {
    @Provides
    fun provideRemoteMyProfileHttpRestDataSourceApi(
        httpRestApi: HttpRestApi
    ): RemoteMyProfileHttpRestDataSourceApi {
        return httpRestApi.myProfileApi
    }
}