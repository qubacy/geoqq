package com.qubacy.geoqq.data._common.repository._common.source.remote.http.rest.token.api.module

import com.qubacy.geoqq.data._common.repository._common.source.remote.http.rest.api.HttpRestApi
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.rest.token.api.RemoteTokenHttpRestDataSourceApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class RemoteTokenHttpRestDataSourceApiModule {
    @Provides
    fun provideRemoteTokenHttpRestDataSourceApi(
        httpRestApi: HttpRestApi
    ): RemoteTokenHttpRestDataSourceApi {
        return httpRestApi.tokenApi
    }
}