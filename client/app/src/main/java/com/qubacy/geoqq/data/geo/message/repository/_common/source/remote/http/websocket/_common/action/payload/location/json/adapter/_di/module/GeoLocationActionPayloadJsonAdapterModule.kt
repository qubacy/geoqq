package com.qubacy.geoqq.data.geo.message.repository._common.source.remote.http.websocket._common.action.payload.location.json.adapter._di.module

import com.qubacy.geoqq.data.geo.message.repository._common.source.remote.http.websocket._common.action.payload.location.GeoLocationActionPayload
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides

@Module
abstract class GeoLocationActionPayloadJsonAdapterModule {
    companion object {
        @JvmStatic
        @Provides
        fun provideGeoLocationActionPayloadJsonAdapter(
            moshi: Moshi
        ): JsonAdapter<GeoLocationActionPayload> {
            return moshi.adapter(GeoLocationActionPayload::class.java)
        }
    }
}