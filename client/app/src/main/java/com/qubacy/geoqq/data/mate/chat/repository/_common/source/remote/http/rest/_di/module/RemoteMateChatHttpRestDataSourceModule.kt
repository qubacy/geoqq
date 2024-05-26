package com.qubacy.geoqq.data.mate.chat.repository._common.source.remote.http.rest._di.module

import com.qubacy.geoqq.data.mate.chat.repository._common.source.remote.http.rest._common.RemoteMateChatHttpRestDataSource
import com.qubacy.geoqq.data.mate.chat.repository._common.source.remote.http.rest.impl.RemoteMateChatHttpRestDataSourceImpl
import dagger.Binds
import dagger.Module

@Module
abstract class RemoteMateChatHttpRestDataSourceModule {
    @Binds
    abstract fun bindRemoteMateChatHttpRestDataSource(
        remoteMateChatHttpRestDataSource: RemoteMateChatHttpRestDataSourceImpl
    ): RemoteMateChatHttpRestDataSource
}