package com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.event.client.json.adapter._di.module

import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.event.client.json.adapter._common.ClientEventJsonAdapter
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.event.client.json.adapter.impl.ClientEventJsonAdapterImpl
import dagger.Binds
import dagger.Module

@Module
abstract class ClientEventJsonAdapterModule {
    @Binds
    abstract fun bindClientEventJsonAdapter(
        clientEventJsonAdapter: ClientEventJsonAdapterImpl
    ): ClientEventJsonAdapter
}