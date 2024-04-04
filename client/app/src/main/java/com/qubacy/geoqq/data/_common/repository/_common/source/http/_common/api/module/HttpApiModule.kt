package com.qubacy.geoqq.data._common.repository._common.source.http._common.api.module

import android.content.Context
import com.qubacy.geoqq.ui.application.CustomApplication
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient

@Module
@InstallIn(SingletonComponent::class)
class HttpApiModule {
    @Provides
    fun provideHttpClient(
        @ApplicationContext context: Context
    ): OkHttpClient {
        val httpApi = (context as CustomApplication).httpApi

        return httpApi.okHttpClient
    }
}