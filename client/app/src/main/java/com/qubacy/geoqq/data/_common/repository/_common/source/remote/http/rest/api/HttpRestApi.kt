package com.qubacy.geoqq.data._common.repository._common.source.remote.http.rest.api

import com.qubacy.geoqq.data._common.repository.token.repository._common.source.remote.http.rest._common.api.RemoteTokenHttpRestDataSourceApi
import com.qubacy.geoqq.data.auth.repository._common.source.remote.http.rest._common.api.RemoteAuthHttpRestDataSourceApi
import com.qubacy.geoqq.data.geo.message.repository._common.source.remote.http.rest._common.api.RemoteGeoMessageHttpRestDataSourceApi
import com.qubacy.geoqq.data.image.repository._common.source.remote.http.rest._common.api.RemoteImageHttpRestDataSourceApi
import com.qubacy.geoqq.data.mate.chat.repository._common.source.remote.http.rest._common.api.RemoteMateChatHttpRestDataSourceApi
import com.qubacy.geoqq.data.mate.message.repository._common.source.remote.http.rest._common.api.RemoteMateMessageHttpRestDataSourceApi
import com.qubacy.geoqq.data.mate.request.repository._common.source.remote.http.rest._common.api.RemoteMateRequestHttpRestDataSourceApi
import com.qubacy.geoqq.data.myprofile.repository._common.source.remote.http.rest._common.api.RemoteMyProfileHttpRestDataSourceApi
import com.qubacy.geoqq.data.user.repository._common.source.remote.http.rest._common.api.RemoteUserHttpRestDataSourceApi
import retrofit2.Retrofit
import javax.inject.Singleton

@Singleton
class HttpRestApi(
    retrofit: Retrofit
) {
    companion object {
        const val TAG = "HttpApi"
    }

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