package com.qubacy.geoqq.data.mate.message.repository._common.source.remote.http.websocket._common.event.payload.added.json.adapter._di.module

import com.qubacy.geoqq.data.mate.message.repository._common.source.remote.http.websocket._common.event.payload.added.MateMessageAddedEventPayload
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides

@Module
abstract class MateMessageAddedEventPayloadJsonAdapterModule {
    companion object {
        @JvmStatic
        @Provides
        fun provideMateMessageAddedEventPayloadJsonAdapter(
            moshi: Moshi
        ): JsonAdapter<MateMessageAddedEventPayload> {
            return moshi.adapter(MateMessageAddedEventPayload::class.java)
        }
    }
}