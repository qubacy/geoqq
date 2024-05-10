package com.qubacy.geoqq.data._common.repository._common.source.remote.http._common.client.module

import com.qubacy.geoqq.data._common.repository._common.source.local.database.error.LocalErrorDataSource
import com.qubacy.geoqq.data._common.repository._common.source.remote.http._common.client.event.HttpCallFailEventListener
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.rest.client.interceptor.auth.AuthorizationHttpRestInterceptor
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
    fun provideHttpClientBuilder(
        localErrorDataSource: LocalErrorDataSource,
        authorizationHttpRestInterceptor: AuthorizationHttpRestInterceptor
    ): OkHttpClient {
        val httpClientRef = AtomicReference<OkHttpClient>()

        val httpClient = OkHttpClient.Builder()
            .callTimeout(10, TimeUnit.SECONDS)
            .connectTimeout(10, TimeUnit.SECONDS)
            .addInterceptor(LanguageHeaderHttpInterceptor())
            .addInterceptor(LoggerHttpInterceptor())
            .addInterceptor(authorizationHttpRestInterceptor)
            .eventListener(HttpCallFailEventListener(localErrorDataSource, httpClientRef))
            .build()

        httpClientRef.set(httpClient)

        return httpClient
    }
}