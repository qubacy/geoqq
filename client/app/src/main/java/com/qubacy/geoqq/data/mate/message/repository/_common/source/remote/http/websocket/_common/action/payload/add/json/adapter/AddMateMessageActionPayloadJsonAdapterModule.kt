package com.qubacy.geoqq.data.mate.message.repository._common.source.remote.http.websocket._common.action.payload.add.json.adapter

import com.qubacy.geoqq.data.mate.message.repository._common.source.remote.http.websocket._common.action.payload.add.AddMateMessageActionPayload
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides

@Module
abstract class AddMateMessageActionPayloadJsonAdapterModule {
    companion object {
        @JvmStatic
        @Provides
        fun provideAddMateMessageActionPayloadJsonAdapter(
            moshi: Moshi
        ): JsonAdapter<AddMateMessageActionPayload> {
            return moshi.adapter(AddMateMessageActionPayload::class.java)
        }
    }
}