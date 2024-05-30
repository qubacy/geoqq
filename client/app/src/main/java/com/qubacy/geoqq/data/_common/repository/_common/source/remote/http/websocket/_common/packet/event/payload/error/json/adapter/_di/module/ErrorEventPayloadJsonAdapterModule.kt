package com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.packet.event.payload.error.json.adapter._di.module

import com.qubacy.geoqq.data._common.repository._common.source.remote.http._common.response.error.content.json.adapter.ErrorResponseContentJsonAdapter
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.packet.event.payload.error.json.adapter.ErrorEventPayloadJsonAdapter
import dagger.Module
import dagger.Provides

@Module
abstract class ErrorEventPayloadJsonAdapterModule {
    companion object {
        @JvmStatic
        @Provides
        fun provideErrorEventPayloadJsonAdapter(
            errorResponseContentJsonAdapter: ErrorResponseContentJsonAdapter
        ): ErrorEventPayloadJsonAdapter {
            return ErrorEventPayloadJsonAdapter(errorResponseContentJsonAdapter)
        }
    }
}