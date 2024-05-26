package com.qubacy.geoqq.data._common.repository._common.source.remote.http._common.response.error.json.adapter.module

import com.qubacy.geoqq.data._common.repository._common.source.remote.http._common.response.error.json.adapter.ErrorJsonAdapter
import dagger.Module
import dagger.Provides

@Module
abstract class ErrorJsonAdapterModule {
    @Provides
    fun provideErrorJsonAdapter(): ErrorJsonAdapter {
        return ErrorJsonAdapter()
    }
}