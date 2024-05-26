package com.qubacy.geoqq.data.user.repository._common.source.remote.http.websocket._common.event.server.payload.updated.json.adapter.module

import com.qubacy.geoqq.data.user.repository._common.source.remote.http.websocket._common.event.server.payload.updated.UserUpdatedServerEventPayload
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides

@Module
abstract class UserUpdatedServerEventPayloadJsonAdapterModule {
    @Provides
    fun provideUserUpdatedServerEventPayloadJsonAdapter(
        moshi: Moshi
    ): JsonAdapter<UserUpdatedServerEventPayload> {
        return moshi.adapter(UserUpdatedServerEventPayload::class.java)
    }
}