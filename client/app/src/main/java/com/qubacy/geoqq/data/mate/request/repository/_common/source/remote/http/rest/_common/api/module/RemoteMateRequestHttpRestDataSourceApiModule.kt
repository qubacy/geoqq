package com.qubacy.geoqq.data.mate.request.repository._common.source.remote.http.rest._common.api.module

import com.qubacy.geoqq.data._common.repository._common.source.remote.http.rest.api.HttpRestApi
import com.qubacy.geoqq.data.mate.request.repository._common.source.remote.http.rest._common.api.RemoteMateRequestHttpRestDataSourceApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object RemoteMateRequestHttpRestDataSourceApiModule {
    @Provides
    fun provideRemoteMateRequestHttpRestDataSourceApi(
        httpRestApi: HttpRestApi
    ): RemoteMateRequestHttpRestDataSourceApi {
        return httpRestApi.mateRequestApi
    }
}