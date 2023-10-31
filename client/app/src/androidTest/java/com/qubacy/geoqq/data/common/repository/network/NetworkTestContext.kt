package com.qubacy.geoqq.data.common.repository.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.Interceptor
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object NetworkTestContext {
    const val BASE_URL = "http://localhost"

    fun generateDefaultTestInterceptor(
        code: Int,
        responseString: String,
    ): Interceptor {
        return object : Interceptor {
            override fun intercept(chain: Interceptor.Chain): Response {
                return Response.Builder()
                    .code(code)
                    .addHeader("content-type", "application/json")
                    .message("message")
                    .request(chain.request())
                    .protocol(Protocol.HTTP_1_1)
                    .body(ResponseBody.create(MediaType.get("application/json"), responseString.toByteArray()))
                    .build()
            }
        }
    }

    fun generateTestRetrofit(testInterceptor: Interceptor): Retrofit {
        val httpClient = OkHttpClient.Builder()
            .addInterceptor(testInterceptor)
            .build()
        val moshi = Moshi.Builder()
            .addLast(KotlinJsonAdapterFactory())
            .build()
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(httpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi).asLenient())
            .build()

        return retrofit
    }
}