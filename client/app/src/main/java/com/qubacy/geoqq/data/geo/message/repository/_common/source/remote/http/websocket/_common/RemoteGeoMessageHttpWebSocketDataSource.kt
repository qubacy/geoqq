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

}