package com.qubacy.geoqq.data.common.repository.common.source.network

import android.os.Build
import android.os.LocaleList
import com.qubacy.geoqq.data.common.repository.common.source.network.model.response.error.ErrorResponse
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.Locale

object NetworkDataSourceContext {
    const val TAG = "NetworkContext"

    const val BASE_URL = "https://4e6e-176-116-169-147.ngrok-free.app"//"http://10.0.2.2:3001"

    private val mOkHttpClient = OkHttpClient.Builder()
        .addInterceptor(LanguageHeaderInterceptor())
//        .addInterceptor {
//            val request = it.request()
//            val response = it.proceed(request)
//
//            Log.d(TAG, response.body()?.string() ?: String())
//
//            response
//        }
        .build()

    private val mMoshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(mOkHttpClient)
        .addConverterFactory(MoshiConverterFactory.create(mMoshi).asLenient())
        .build()

    val errorResponseJsonAdapter = mMoshi.adapter(ErrorResponse::class.java)

    class LanguageHeaderInterceptor() : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response = chain.run {
            proceed(
                request().newBuilder()
                    .addHeader("Accept-Language", getLanguage())
                    .build()
            )
        }

        private fun getLanguage(): String {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                LocaleList.getDefault().toLanguageTags();
            } else {
                Locale.getDefault().language;
            }
        }
    }
}