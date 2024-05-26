package com.qubacy.geoqq.data._common.repository._common.source.remote.http._common.response.error.json.adapter._di.module

import com.qubacy.geoqq.data._common.repository._common.source.remote.http._common.response.error.json.adapter.ErrorJsonAdapter
import dagger.Module
import dagger.Provides

@Module
abstract class ErrorJsonAdapterModule {
    companion object {
        @JvmStatic
        @Provides
        fun provideErrorJsonAdapter(): ErrorJsonAdapter {
            return ErrorJsonAdapter()
        }
    }
}