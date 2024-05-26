package com.qubacy.geoqq.data.myprofile.repository._common.source.remote.http.rest._common.api.module

import com.qubacy.geoqq.data._common.repository._common.source.remote.http.rest.api.HttpRestApi
import com.qubacy.geoqq.data.myprofile.repository._common.source.remote.http.rest._common.api.RemoteMyProfileHttpRestDataSourceApi
import dagger.Module
import dagger.Provides

@Module
abstract class RemoteMyProfileHttpRestDataSourceModuleApi {
    @Provides
    fun provideRemoteMyProfileHttpRestDataSourceApi(
        httpRestApi: HttpRestApi
    ): RemoteMyProfileHttpRestDataSourceApi {
        return httpRestApi.myProfileApi
    }
}