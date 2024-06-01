package com.qubacy.geoqq.data.mate.chat.repository._common.source.remote.http.websocket._common.event.payload.updated.json.adapter._di.module

import com.qubacy.geoqq.data.mate.chat.repository._common.source.remote.http.websocket._common.event.payload.updated.MateChatEventPayload
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides

@Module
abstract class MateChatEventPayloadJsonAdapterModule {
    companion object {
        @JvmStatic
        @Provides
        fun provideMateChatEventPayloadJsonAdapter(
           moshi: Moshi
        ): JsonAdapter<MateChatEventPayload> {
            return moshi.adapter(MateChatEventPayload::class.java)
        }
    }
}