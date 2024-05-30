package com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.packet.action.json.adapter._di.module

import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.packet.action.json.adapter._common.ActionJsonAdapter
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.packet.action.json.adapter.impl.ActionJsonAdapterImpl
import dagger.Binds
import dagger.Module

@Module
abstract class ActionJsonAdapterModule {
    @Binds
    abstract fun bindActionJsonAdapter(
        clientEventJsonAdapter: ActionJsonAdapterImpl
    ): ActionJsonAdapter
}