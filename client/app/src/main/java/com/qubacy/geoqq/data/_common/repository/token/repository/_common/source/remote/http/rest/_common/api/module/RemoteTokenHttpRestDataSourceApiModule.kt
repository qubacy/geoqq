package com.qubacy.geoqq.data._common.repository.token.repository._common.source.remote.http.rest._common.api.module

import com.qubacy.geoqq.data._common.repository._common.source.remote.http.rest.api.HttpRestApi
import com.qubacy.geoqq.data._common.repository.token.repository._common.source.remote.http.rest._common.api.RemoteTokenHttpRestDataSourceApi
import dagger.Module
import dagger.Provides

@Module
abstract class RemoteTokenHttpRestDataSourceApiModule {
    @Provides
    fun provideRemoteTokenHttpRestDataSourceApi(
        httpRestApi: HttpRestApi
    ): RemoteTokenHttpRestDataSourceApi {
        return httpRestApi.tokenApi
    }
}