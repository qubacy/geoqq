package com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.packet.event.payload.success.json.adapter._di.module

import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.packet.event.payload.success.json.adapter.SuccessEventPayloadJsonAdapter
import dagger.Module
import dagger.Provides

@Module
abstract class SuccessEventPayloadJsonAdapterModule {
    companion object {
        @JvmStatic
        @Provides
        fun provideSuccessEventPayloadJsonAdapter(): SuccessEventPayloadJsonAdapter {
            return SuccessEventPayloadJsonAdapter()
        }
    }
}