package com.qubacy.geoqq.data._common.repository._common.source.remote.http.rest.api.module

import android.content.Context
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.rest.api.HttpRestApi
import com.qubacy.geoqq.ui.application.CustomApplication
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object HttpRestApiModule {
    @Provides
    fun provideHttpRestApi(
        @ApplicationContext context: Context
    ): HttpRestApi {
        val httpRestApi = (context as CustomApplication).httpRestApi

        return httpRestApi
    }
}