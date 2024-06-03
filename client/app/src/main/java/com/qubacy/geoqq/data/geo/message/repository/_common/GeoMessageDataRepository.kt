package com.qubacy.geoqq.data.geo.message.repository._common

import androidx.lifecycle.LiveData
import com.qubacy.geoqq.data._common.repository.aspect.websocket.WebSocketEventDataRepository
import com.qubacy.geoqq.data._common.repository.message.MessageDataRepository
import com.qubacy.geoqq.data._common.repository.producing.ProducingDataRepository
import com.qubacy.geoqq.data.geo.message.repository._common.result.get.GetGeoMessagesDataResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

abstract class GeoMessageDataRepository(
    coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO,
    coroutineScope: CoroutineScope = CoroutineScope(coroutineDispatcher)
) : ProducingDataRepository(coroutineDispatcher, coroutineScope),
    MessageDataRepository,
    WebSocketEventDataRepository
{
    abstract suspend fun getMessages(
        radius: Int,
        longitude: Float,
        latitude: Float
    ): LiveData<GetGeoMessagesDataResult>
    abstract suspend fun sendMessage(
        text: String,
        longitude: Float,
        latitude: Float
    )
    abstract suspend fun sendLocation(
        longitude: Float,
        latitude: Float,
        radius: Int
    )
}