package com.qubacy.geoqq.data._common.repository._common.source.remote.http.rest.api

import com.qubacy.geoqq.data._common.repository._common.source.remote.http._common.response._common.json.adapter.StringJsonAdapter
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.rest.token._common.api.RemoteTokenHttpRestDataSourceApi
import com.qubacy.geoqq.data.auth.repository._common.source.remote.http.rest._common.api.RemoteAuthHttpRestDataSourceApi
import com.qubacy.geoqq.data.geo.message.repository._common.source.remote.http.rest._common.api.RemoteGeoMessageHttpRestDataSourceApi
import com.qubacy.geoqq.data.image.repository._common.source.remote.http.rest._common.api.RemoteImageHttpRestDataSourceApi
import com.qubacy.geoqq.data.mate.chat.repository._common.source.remote.http.rest._common.api.RemoteMateChatHttpRestDataSourceApi
import com.qubacy.geoqq.data.mate.message.repository._common.source.remote.http.rest._common.api.RemoteMateMessageHttpRestDataSourceApi
import com.qubacy.geoqq.data.mate.request.repository._common.source.remote.http.rest._common.api.RemoteMateRequestHttpRestDataSourceApi
import com.qubacy.geoqq.data.myprofile.repository._common.source.remote.http.rest._common.api.RemoteMyProfileHttpRestDataSourceApi
import com.qubacy.geoqq.data.user.repository._common.source.remote.http.rest._common.api.RemoteUserHttpRestDataSourceApi
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

    val tokenApi: RemoteTokenHttpRestDataSourceApi = retrofit.create(
        RemoteTokenHttpRestDataSourceApi::class.java)
    val authApi: RemoteAuthHttpRestDataSourceApi = retrofit.create(RemoteAuthHttpRestDataSourceApi::class.java)

    val mateMessageApi: RemoteMateMessageHttpRestDataSourceApi =
        retrofit.create(RemoteMateMessageHttpRestDataSourceApi::class.java)
    val mateChatApi: RemoteMateChatHttpRestDataSourceApi =
        retrofit.create(RemoteMateChatHttpRestDataSourceApi::class.java)
    val mateRequestApi: RemoteMateRequestHttpRestDataSourceApi =
        retrofit.create(RemoteMateRequestHttpRestDataSourceApi::class.java)

    val userApi: RemoteUserHttpRestDataSourceApi = retrofit.create(RemoteUserHttpRestDataSourceApi::class.java)
    val imageApi: RemoteImageHttpRestDataSourceApi = retrofit.create(
        RemoteImageHttpRestDataSourceApi::class.java)

    val myProfileApi: RemoteMyProfileHttpRestDataSourceApi = retrofit.create(
        RemoteMyProfileHttpRestDataSourceApi::class.java)
    val geoChatApi: RemoteGeoMessageHttpRestDataSourceApi = retrofit.create(
        RemoteGeoMessageHttpRestDataSourceApi::class.java)
}