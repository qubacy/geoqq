package com.qubacy.geoqq.data.geo.message.repository._common.source.remote.http.websocket._common

import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.message.RemoteHttpWebSocketMessageDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi

abstract class RemoteGeoMessageHttpWebSocketDataSource @OptIn(ExperimentalCoroutinesApi::class) constructor(
    coroutineDispatcher: CoroutineDispatcher = Dispatchers.Default.limitedParallelism(1),
    coroutineScope: CoroutineScope = CoroutineScope(coroutineDispatcher)
) : RemoteHttpWebSocketMessageDataSource(coroutineDispatcher, coroutineScope) {
    companion object {
        const val ADD_GEO_MESSAGE_FAILED_EVENT = "add_geo_message_failed"
        const val UPDATE_USER_LOCATION_FAILED_EVENT = "update_user_location_failed"
    }

    abstract fun sendMessage(text: String, latitude: Float, longitude: Float)
    abstract fun sendLocation(latitude: Float, longitude: Float, radius: Int)
    override fun isErrorMessageEventConsumable(event: String): Boolean {
        return event in arrayOf(
            ADD_GEO_MESSAGE_FAILED_EVENT,
            UPDATE_USER_LOCATION_FAILED_EVENT
        )
    }
}