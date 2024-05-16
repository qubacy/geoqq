package com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.event.client.json.adapter.module

import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.event.client.json.adapter.ClientEventJsonAdapter
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object ClientEventJsonAdapterModule {
    @Provides
    fun provideClientEventJsonAdapter(): ClientEventJsonAdapter {
        return ClientEventJsonAdapter()
    }
}