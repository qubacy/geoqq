package com.qubacy.geoqq.data._common.repository._common.source.remote.http.api

import android.os.Build
import android.os.LocaleList
import com.qubacy.geoqq.data.geo.message.repository.source.http.HttpGeoChatDataSource
import com.qubacy.geoqq.data.image.repository.source.http.HttpImageDataSource
import com.qubacy.geoqq.data.mate.chat.repository.source.http.HttpMateChatDataSource
import com.qubacy.geoqq.data.mate.message.repository.source.http.HttpMateMessageDataSource
import com.qubacy.geoqq.data.mate.request.repository.source.http.HttpMateRequestDataSource
import com.qubacy.geoqq.data.myprofile.repository.source.http.HttpMyProfileDataSource
import com.qubacy.geoqq.data.token.repository.source.http.HttpTokenDataSource
import com.qubacy.geoqq.data.user.repository.source.http.HttpUserDataSource
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.Locale
import java.util.concurrent.TimeUnit

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

        const val BASE_URL = "https://75bc-5-101-44-221.ngrok-free.app"//"http://10.0.2.2:3001"
    }

    /**
     * NOTE: uncomment the logging interceptor ONLY for debugging purposes.
     */
    val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(LanguageHeaderInterceptor())
        .callTimeout(10, TimeUnit.SECONDS)
        .connectTimeout(10, TimeUnit.SECONDS)
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
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create(mMoshi).asLenient())
        .build()

    val tokenApi: HttpTokenDataSource = retrofit.create(HttpTokenDataSource::class.java)
    val mateMessageApi: HttpMateMessageDataSource =
        retrofit.create(HttpMateMessageDataSource::class.java)
    val mateChatApi: HttpMateChatDataSource =
        retrofit.create(HttpMateChatDataSource::class.java)
    val mateRequestApi: HttpMateRequestDataSource =
        retrofit.create(HttpMateRequestDataSource::class.java)
    val userApi: HttpUserDataSource = retrofit.create(HttpUserDataSource::class.java)
    val imageApi: HttpImageDataSource = retrofit.create(HttpImageDataSource::class.java)
    val myProfileApi: HttpMyProfileDataSource = retrofit.create(HttpMyProfileDataSource::class.java)
    val geoChatApi: HttpGeoChatDataSource = retrofit.create(HttpGeoChatDataSource::class.java)
}