package com.qubacy.geoqq.data.mate.chat.repository._common.source.remote.http.rest._common.api.module

import com.qubacy.geoqq.data._common.repository._common.source.remote.http.rest.api.HttpRestApi
import com.qubacy.geoqq.data.mate.chat.repository._common.source.remote.http.rest._common.api.RemoteMateChatHttpRestDataSourceApi
import dagger.Module
import dagger.Provides

@Module
abstract class RemoteMateChatHttpRestDataSourceApiModule {
    @Provides
    fun bindRemoteMateChatHttpRestDataSourceApi(
        httpRestApi: HttpRestApi
    ): RemoteMateChatHttpRestDataSourceApi {
        return httpRestApi.mateChatApi
    }
}