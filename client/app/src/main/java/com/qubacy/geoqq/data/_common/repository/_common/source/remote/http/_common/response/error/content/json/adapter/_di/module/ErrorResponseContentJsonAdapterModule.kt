package com.qubacy.geoqq.data._common.repository._common.source.remote.http._common.response.error.content.json.adapter._di.module

import com.qubacy.geoqq.data._common.repository._common.source.remote.http._common.response.error.content.json.adapter.ErrorResponseContentJsonAdapter
import dagger.Module
import dagger.Provides

@Module
abstract class ErrorResponseContentJsonAdapterModule {
    companion object {
        @JvmStatic
        @Provides
        fun provideErrorResponseContentJsonAdapter(): ErrorResponseContentJsonAdapter {
            return ErrorResponseContentJsonAdapter()
        }
    }
}