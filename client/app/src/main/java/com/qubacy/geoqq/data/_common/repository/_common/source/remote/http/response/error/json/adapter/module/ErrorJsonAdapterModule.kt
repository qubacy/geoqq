package com.qubacy.geoqq.data._common.repository._common.source.remote.http.response.error.json.adapter.module

import com.qubacy.geoqq.data._common.repository._common.source.remote.http.response.error.json.adapter.ErrorJsonAdapter
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class ErrorJsonAdapterModule {
    @Provides
    fun provideErrorJsonAdapter(): ErrorJsonAdapter {
        return ErrorJsonAdapter()
    }
}