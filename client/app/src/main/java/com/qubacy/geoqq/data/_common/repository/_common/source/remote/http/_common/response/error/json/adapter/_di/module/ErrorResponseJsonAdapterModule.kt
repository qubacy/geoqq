package com.qubacy.geoqq.data._common.repository._common.source.remote.http._common.response.error.json.adapter._di.module

import com.qubacy.geoqq.data._common.repository._common.source.remote.http._common.response.error.content.json.adapter.ErrorResponseContentJsonAdapter
import com.qubacy.geoqq.data._common.repository._common.source.remote.http._common.response.error.json.adapter.ErrorResponseJsonAdapter
import dagger.Module
import dagger.Provides

@Module
abstract class ErrorResponseJsonAdapterModule {
    companion object {
        @JvmStatic
        @Provides
        fun provideErrorResponseJsonAdapter(
            errorResponseContentJsonAdapter: ErrorResponseContentJsonAdapter
        ): ErrorResponseJsonAdapter {
            return ErrorResponseJsonAdapter(errorResponseContentJsonAdapter)
        }
    }
}