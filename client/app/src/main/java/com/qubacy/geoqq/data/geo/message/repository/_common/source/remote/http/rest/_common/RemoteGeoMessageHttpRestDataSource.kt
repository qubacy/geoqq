package com.qubacy.geoqq.data.geo.message.repository._common.source.remote.http.rest._common

import com.qubacy.geoqq.data._common.repository.message.source.remote.http.response.GetMessagesResponse

interface RemoteGeoMessageHttpRestDataSource {
    fun getMessages(
        radius: Int,
        longitude: Float,
        latitude: Float
    ): GetMessagesResponse
    fun sendMessage(
        text: String,
        radius: Int,
        longitude: Float,
        latitude: Float
    )
}