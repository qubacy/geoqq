package com.qubacy.geoqq.data._common.repository._common.source.remote.http._common.client.module

import com.qubacy.geoqq.data._common.repository._common.source.local.database.error._common.LocalErrorDatabaseDataSource
import com.qubacy.geoqq.data._common.repository._common.source.remote.http._common.client.event.HttpCallFailEventListener
import com.qubacy.geoqq.data._common.repository._common.source.remote.http._common.client.interceptor.lang.LanguageHeaderHttpInterceptor
import com.qubacy.geoqq.data._common.repository._common.source.remote.http._common.client.interceptor.logger.LoggerHttpInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference

@Module
@InstallIn(SingletonComponent::class)
object HttpClientModule {
    @Provides
    fun provideHttpClient(
        localErrorDataSource: LocalErrorDatabaseDataSource
    ): OkHttpClient {
        val httpClientRef = AtomicReference<OkHttpClient>()

        val httpClient = OkHttpClient.Builder()
            .callTimeout(10, TimeUnit.SECONDS)
            .connectTimeout(10, TimeUnit.SECONDS)
            .addInterceptor(LanguageHeaderHttpInterceptor())
            .addInterceptor(LoggerHttpInterceptor())
            .eventListener(HttpCallFailEventListener(localErrorDataSource, httpClientRef))
            .build()

        httpClientRef.set(httpClient)

        return httpClient
    }
}