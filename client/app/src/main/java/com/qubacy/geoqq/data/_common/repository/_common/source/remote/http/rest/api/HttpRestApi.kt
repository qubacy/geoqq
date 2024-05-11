package com.qubacy.geoqq.data._common.repository._common.source.remote.http.rest.api

import com.qubacy.geoqq.data._common.repository._common.source.remote.http._common.response._common.json.adapter.StringJsonAdapter
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.rest.token.api.RemoteTokenHttpRestDataSourceApi
import com.qubacy.geoqq.data.auth.repository.source.http.api.HttpAuthDataSourceApi
import com.qubacy.geoqq.data.geo.message.repository.source.http.api.HttpGeoMessageDataSourceApi
import com.qubacy.geoqq.data.image.repository.source.http.api.HttpImageDataSourceApi
import com.qubacy.geoqq.data.mate.chat.repository.source.http.api.HttpMateChatDataSourceApi
import com.qubacy.geoqq.data.mate.message.repository.source.http.api.HttpMateMessageDataSourceApi
import com.qubacy.geoqq.data.mate.request.repository.source.http.api.HttpMateRequestDataSourceApi
import com.qubacy.geoqq.data.myprofile.repository.source.http.api.HttpMyProfileDataSourceApi
import com.qubacy.geoqq.data.user.repository.source.http.api.HttpUserDataSourceApi
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class HttpRestApi(httpClient: OkHttpClient) {
    companion object {
        const val TAG = "HttpApi"

        const val BASE_URL = "https://847c-5-101-44-221.ngrok-free.app"//"http://10.0.2.2:3001"
    }

    val okHttpClient = httpClient

    private val mMoshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .add(String::class.java, StringJsonAdapter())
        .build()

    val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create(mMoshi).asLenient())
        .build()

    val tokenApi: RemoteTokenHttpRestDataSourceApi = retrofit.create(RemoteTokenHttpRestDataSourceApi::class.java)
    val authApi: HttpAuthDataSourceApi = retrofit.create(HttpAuthDataSourceApi::class.java)

    val mateMessageApi: HttpMateMessageDataSourceApi =
        retrofit.create(HttpMateMessageDataSourceApi::class.java)
    val mateChatApi: HttpMateChatDataSourceApi =
        retrofit.create(HttpMateChatDataSourceApi::class.java)
    val mateRequestApi: HttpMateRequestDataSourceApi =
        retrofit.create(HttpMateRequestDataSourceApi::class.java)

    val userApi: HttpUserDataSourceApi = retrofit.create(HttpUserDataSourceApi::class.java)
    val imageApi: HttpImageDataSourceApi = retrofit.create(HttpImageDataSourceApi::class.java)

    val myProfileApi: HttpMyProfileDataSourceApi = retrofit.create(HttpMyProfileDataSourceApi::class.java)
    val geoChatApi: HttpGeoMessageDataSourceApi = retrofit.create(HttpGeoMessageDataSourceApi::class.java)
}