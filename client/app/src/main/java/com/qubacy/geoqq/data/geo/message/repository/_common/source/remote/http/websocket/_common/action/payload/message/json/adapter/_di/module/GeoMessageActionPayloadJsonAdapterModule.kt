package com.qubacy.geoqq.data.geo.message.repository._common.source.remote.http.websocket._common.action.payload.message.json.adapter._di.module

import com.qubacy.geoqq.data.geo.message.repository._common.source.remote.http.websocket._common.action.payload.message.GeoMessageActionPayload
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides

@Module
abstract class GeoMessageActionPayloadJsonAdapterModule {
    companion object {
        @JvmStatic
        @Provides
        fun provideGeoMessageActionPayloadJsonAdapter(
            moshi: Moshi
        ): JsonAdapter<GeoMessageActionPayload> {
            return moshi.adapter(GeoMessageActionPayload::class.java)
        }
    }
}