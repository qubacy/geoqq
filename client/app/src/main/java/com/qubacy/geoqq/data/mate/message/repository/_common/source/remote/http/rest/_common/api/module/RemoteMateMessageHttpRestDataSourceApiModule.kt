package com.qubacy.geoqq.data.mate.message.repository._common.source.remote.http.rest._common.api.module

import com.qubacy.geoqq.data._common.repository._common.source.remote.http.rest.api.HttpRestApi
import com.qubacy.geoqq.data.mate.message.repository._common.source.remote.http.rest._common.api.RemoteMateMessageHttpRestDataSourceApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object RemoteMateMessageHttpRestDataSourceApiModule {
    @Provides
    fun provideRemoteMateMessageHttpRestDataSourceApi(
        httpRestApi: HttpRestApi
    ): RemoteMateMessageHttpRestDataSourceApi {
        return httpRestApi.mateMessageApi
    }
}