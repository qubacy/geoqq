package com.qubacy.geoqq.data.user.repository._common.source.remote.http.websocket._common.event.payload.updated.json.adapter._di.module

import com.qubacy.geoqq.data.user.repository._common.source.remote.http.websocket._common.event.payload.updated.UserUpdatedEventPayload
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides

@Module
abstract class UserUpdatedEventPayloadJsonAdapterModule {
    companion object {
        @JvmStatic
        @Provides
        fun provideUserUpdatedEventPayloadJsonAdapter(
            moshi: Moshi
        ): JsonAdapter<UserUpdatedEventPayload> {
            return moshi.adapter(UserUpdatedEventPayload::class.java)
        }
    }
}