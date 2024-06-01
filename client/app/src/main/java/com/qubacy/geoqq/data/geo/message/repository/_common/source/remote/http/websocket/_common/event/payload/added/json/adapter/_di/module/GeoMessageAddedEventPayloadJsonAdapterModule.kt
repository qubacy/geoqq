package com.qubacy.geoqq.data.geo.message.repository._common.source.remote.http.websocket._common.event.payload.added.json.adapter._di.module

import com.qubacy.geoqq.data.geo.message.repository._common.source.remote.http.websocket._common.event.payload.added.GeoMessageAddedEventPayload
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides

@Module
abstract class GeoMessageAddedEventPayloadJsonAdapterModule {
    companion object {
        @JvmStatic
        @Provides
        fun provideGeoMessageAddedEventPayloadJsonAdapter(
            moshi: Moshi
        ): JsonAdapter<GeoMessageAddedEventPayload> {
            return moshi.adapter(GeoMessageAddedEventPayload::class.java)
        }
    }
}