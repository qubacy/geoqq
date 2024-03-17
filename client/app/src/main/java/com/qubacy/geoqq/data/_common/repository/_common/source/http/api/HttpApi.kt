package com.qubacy.geoqq.data._common.repository._common.source.http.api

import android.os.Build
import android.os.LocaleList
import android.util.Log
import com.qubacy.geoqq.data.token.repository.source.http.HttpTokenDataSource
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.Locale

class HttpApi {
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

    companion object {
        const val TAG = "HttpApi"

        const val BASE_URL = "http://10.0.2.2:3001"
    }

    /**
     * NOTE: uncomment the logging interceptor ONLY for debugging purposes.
     */
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

    val tokenApi: HttpTokenDataSource = retrofit.create(HttpTokenDataSource::class.java)
}