package com.qubacy.geoqq.data.geo.message.repository.source.http

import com.qubacy.geoqq.data._common.repository._common.source.remote.http.executor.HttpCallExecutor
import com.qubacy.geoqq.data._common.repository.message.source.remote.http.response.GetMessagesResponse
import com.qubacy.geoqq.data.geo.message.repository.source.http.api.HttpGeoMessageDataSourceApi
import com.qubacy.geoqq.data.geo.message.repository.source.http.api.request.SendMessageRequest
import javax.inject.Inject

class HttpGeoMessageDataSource @Inject constructor(
    private val mHttpGeoMessageDataSourceApi: HttpGeoMessageDataSourceApi,
    private val mHttpCallExecutor: HttpCallExecutor
) {
    fun getMessages(
        radius: Int,
        longitude: Float,
        latitude: Float
    ): GetMessagesResponse {
        val getMessagesCall = mHttpGeoMessageDataSourceApi.getMessages(radius, longitude, latitude)
        val getMessagesResponse = mHttpCallExecutor.executeNetworkRequest(getMessagesCall)

        return getMessagesResponse
    }

    fun sendMessage(
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