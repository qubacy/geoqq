package com.qubacy.geoqq.data._common.repository._common.source.remote.http.client.module

import com.qubacy.geoqq.data._common.repository._common.source.local.database.error.LocalErrorDataSource
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.client.event.HttpCallFailEventListener
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.client.interceptor.auth.AuthorizationHttpInterceptor
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.client.interceptor.lang.LanguageHeaderHttpInterceptor
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.client.interceptor.logger.LoggerHttpInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Call
import okhttp3.EventListener
import okhttp3.OkHttpClient
import java.io.IOException
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference

@Module
@InstallIn(SingletonComponent::class)
object HttpClientModule {
    @Provides
    fun provideHttpClient(
        localErrorDataSource: LocalErrorDataSource,
        authorizationHttpInterceptor: AuthorizationHttpInterceptor
    ): OkHttpClient {
        val httpClientRef = AtomicReference<OkHttpClient>()

        val httpClient = OkHttpClient.Builder()
            .callTimeout(10, TimeUnit.SECONDS)
            .connectTimeout(10, TimeUnit.SECONDS)
            .addInterceptor(LanguageHeaderHttpInterceptor())
            .addInterceptor(LoggerHttpInterceptor())
            .addInterceptor(authorizationHttpInterceptor)
            .eventListener(HttpCallFailEventListener(localErrorDataSource, httpClientRef))
            .build()

        httpClientRef.set(httpClient)

        return httpClient
    }
}