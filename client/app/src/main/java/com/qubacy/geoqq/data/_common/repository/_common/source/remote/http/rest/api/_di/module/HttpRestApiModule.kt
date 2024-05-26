package com.qubacy.geoqq.data._common.repository._common.source.remote.http.rest.api._di.module

import com.qubacy.geoqq.data._common.repository._common.source.remote.http.rest.api.HttpRestApi
import com.qubacy.geoqq.data._common.repository.token.repository._common.source.remote.http.rest._common.RemoteTokenHttpRestDataSource
import com.qubacy.geoqq.data._common.repository.token.repository._common.source.remote.http.rest.impl.RemoteTokenHttpRestDataSourceImpl
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit

@Module
abstract class HttpRestApiModule {
    companion object {
        @JvmStatic
        @Provides
        fun provideHttpRestApi(
            remoteTokenHttpRestDataSource: RemoteTokenHttpRestDataSource,
            retrofit: Retrofit
        ): HttpRestApi {
            return HttpRestApi(retrofit).apply {
                // todo: isn't it dirty?:
                if (remoteTokenHttpRestDataSource is RemoteTokenHttpRestDataSourceImpl)
                    remoteTokenHttpRestDataSource.setHttpTokenDataSourceApi(this.tokenApi)
            }
        }
    }
}