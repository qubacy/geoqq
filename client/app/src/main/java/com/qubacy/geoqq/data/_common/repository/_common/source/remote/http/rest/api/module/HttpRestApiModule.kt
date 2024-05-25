package com.qubacy.geoqq.data._common.repository._common.source.remote.http.rest.api.module

import android.content.Context
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.rest.api.HttpRestApi
import com.qubacy.geoqq.ui.application.CustomApplication
import dagger.Module
import dagger.Provides

@Module
object HttpRestApiModule {
    @Provides
    fun provideHttpRestApi(
        context: Context
    ): HttpRestApi {
        val httpRestApi = (context as CustomApplication).httpRestApi

        return httpRestApi
    }
}