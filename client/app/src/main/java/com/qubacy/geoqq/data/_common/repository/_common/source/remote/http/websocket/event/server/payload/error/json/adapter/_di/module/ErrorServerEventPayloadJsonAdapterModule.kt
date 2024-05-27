package com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.event.server.payload.error.json.adapter._di.module

import com.qubacy.geoqq.data._common.repository._common.source.remote.http._common.response.error.content.json.adapter.ErrorResponseContentJsonAdapter
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.event.server.payload.error.json.adapter.ErrorServerEventPayloadJsonAdapter
import dagger.Module
import dagger.Provides

@Module
abstract class ErrorServerEventPayloadJsonAdapterModule {
    companion object {
        @JvmStatic
        @Provides
        fun provideErrorServerEventPayloadJsonAdapter(
            errorResponseContentJsonAdapter: ErrorResponseContentJsonAdapter
        ): ErrorServerEventPayloadJsonAdapter {
            return ErrorServerEventPayloadJsonAdapter(errorResponseContentJsonAdapter)
        }
    }
}