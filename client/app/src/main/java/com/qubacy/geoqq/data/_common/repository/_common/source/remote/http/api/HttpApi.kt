package com.qubacy.geoqq.data._common.repository._common.source.remote.http.api

import com.qubacy.geoqq.data._common.repository._common.source.remote.http.response._common.json.adapter.StringJsonAdapter
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.token.api.HttpTokenDataSourceApi
import com.qubacy.geoqq.data.geo.message.repository.source.http.api.HttpGeoChatDataSourceApi
import com.qubacy.geoqq.data.image.repository.source.http.api.HttpImageDataSourceApi
import com.qubacy.geoqq.data.mate.chat.repository.source.http.HttpMateChatDataSource
import com.qubacy.geoqq.data.mate.message.repository.source.http.HttpMateMessageDataSource
import com.qubacy.geoqq.data.mate.request.repository.source.http.HttpMateRequestDataSource
import com.qubacy.geoqq.data.myprofile.repository.source.http.HttpMyProfileDataSource
import com.qubacy.geoqq.data.user.repository.source.http.api.HttpUserDataSourceApi
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class HttpApi(httpClient: OkHttpClient) {
    companion object {
        const val TAG = "HttpApi"

        const val BASE_URL = "https://0e36-5-101-44-221.ngrok-free.app"//"http://10.0.2.2:3001"
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

    val tokenApi: HttpTokenDataSourceApi = retrofit.create(HttpTokenDataSourceApi::class.java)
    val mateMessageApi: HttpMateMessageDataSource =
        retrofit.create(HttpMateMessageDataSource::class.java)
    val mateChatApi: HttpMateChatDataSource =
        retrofit.create(HttpMateChatDataSource::class.java)
    val mateRequestApi: HttpMateRequestDataSource =
        retrofit.create(HttpMateRequestDataSource::class.java)
    val userApi: HttpUserDataSourceApi = retrofit.create(HttpUserDataSourceApi::class.java)
    val imageApi: HttpImageDataSourceApi = retrofit.create(HttpImageDataSourceApi::class.java)
    val myProfileApi: HttpMyProfileDataSource = retrofit.create(HttpMyProfileDataSource::class.java)
    val geoChatApi: HttpGeoChatDataSourceApi = retrofit.create(HttpGeoChatDataSourceApi::class.java)
}