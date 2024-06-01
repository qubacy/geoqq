package com.qubacy.geoqq.data.mate.request.repository._common.source.remote.http.websocket._common.event.payload.added.json.adapter._di.module

import com.qubacy.geoqq.data.mate.request.repository._common.source.remote.http.websocket._common.event.payload.added.MateRequestAddedEventPayload
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides

@Module
abstract class MateRequestAddedEventPayloadJsonAdapterModule {
    companion object {
        @JvmStatic
        @Provides
        fun provideMateRequestAddedEventPayloadJsonAdapter(
            moshi: Moshi
        ): JsonAdapter<MateRequestAddedEventPayload> {
            return moshi.adapter(MateRequestAddedEventPayload::class.java)
        }
    }
}