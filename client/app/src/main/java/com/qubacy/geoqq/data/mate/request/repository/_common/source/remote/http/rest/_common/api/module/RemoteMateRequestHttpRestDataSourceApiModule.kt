package com.qubacy.geoqq.data.mate.request.repository._common.source.remote.http.rest._common.api.module

import com.qubacy.geoqq.data._common.repository._common.source.remote.http.rest.api.HttpRestApi
import com.qubacy.geoqq.data.mate.request.repository._common.source.remote.http.rest._common.api.RemoteMateRequestHttpRestDataSourceApi
import dagger.Module
import dagger.Provides

@Module
abstract class RemoteMateRequestHttpRestDataSourceApiModule {
    @Provides
    fun provideRemoteMateRequestHttpRestDataSourceApi(
        httpRestApi: HttpRestApi
    ): RemoteMateRequestHttpRestDataSourceApi {
        return httpRestApi.mateRequestApi
    }
}