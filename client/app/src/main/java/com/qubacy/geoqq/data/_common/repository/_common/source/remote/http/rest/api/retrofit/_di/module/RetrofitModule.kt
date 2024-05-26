package com.qubacy.geoqq.data._common.repository._common.source.remote.http.rest.api.retrofit._di.module

import com.qubacy.geoqq.data._common.repository._common.source.remote.http._common.context.HttpContext
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.rest.client.interceptor.auth._common.AuthorizationHttpRestInterceptor
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

@Module
abstract class RetrofitModule {
    companion object {
        @JvmStatic
        @Provides
        fun provideRetrofit(
            okHttpClient: OkHttpClient,
            moshi: Moshi,
            authorizationHttpRestInterceptor: AuthorizationHttpRestInterceptor
        ): Retrofit {
            val restHttpClient = okHttpClient
                .newBuilder()
                .addInterceptor(authorizationHttpRestInterceptor)
                .build()

            return Retrofit.Builder()
                .baseUrl(HttpContext.BASE_URL)
                .client(restHttpClient)
                .addConverterFactory(MoshiConverterFactory.create(moshi).asLenient())
                .build()
        }
    }
}