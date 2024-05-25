package com.qubacy.geoqq.data.geo.message.repository._common.source.remote.http.rest.impl

import com.qubacy.geoqq.data._common.repository._common.source.remote.http._common.executor.impl.HttpCallExecutorImpl
import com.qubacy.geoqq.data._common.repository.message.source.remote.http.response.GetMessagesResponse
import com.qubacy.geoqq.data.geo.message.repository._common.source.remote.http.rest._common.RemoteGeoMessageHttpRestDataSource
import com.qubacy.geoqq.data.geo.message.repository._common.source.remote.http.rest._common.api.RemoteGeoMessageHttpRestDataSourceApi
import com.qubacy.geoqq.data.geo.message.repository._common.source.remote.http.rest._common.api.request.SendMessageRequest
import javax.inject.Inject

class RemoteGeoMessageHttpRestDataSourceImpl @Inject constructor(
    private val mHttpGeoMessageDataSourceApi: RemoteGeoMessageHttpRestDataSourceApi,
    private val mHttpCallExecutor: HttpCallExecutorImpl
) : RemoteGeoMessageHttpRestDataSource {
    override fun getMessages(
        radius: Int,
        longitude: Float,
        latitude: Float
    ): GetMessagesResponse {
        val getMessagesCall = mHttpGeoMessageDataSourceApi.getMessages(radius, longitude, latitude)
        val getMessagesResponse = mHttpCallExecutor.executeNetworkRequest(getMessagesCall)

        return getMessagesResponse
    }

    override fun sendMessage(
        text: String,
        radius: Int,
        longitude: Float,
        latitude: Float
    ) {
        val sendMessageRequest = SendMessageRequest(text, radius, longitude, latitude)
        val sendMessageCall = mHttpGeoMessageDataSourceApi.sendMessage(sendMessageRequest)

        mHttpCallExecutor.executeNetworkRequest(sendMessageCall)
    }
}