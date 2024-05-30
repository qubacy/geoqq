package com.qubacy.geoqq.data.auth.repository._common

import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.socket.adapter._common.WebSocketAdapter
import com.qubacy.geoqq.data._common.repository.producing.ProducingDataRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

abstract class AuthDataRepository(
    coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO,
    coroutineScope: CoroutineScope = CoroutineScope(coroutineDispatcher)
) : ProducingDataRepository(coroutineDispatcher, coroutineScope) {
    abstract val webSocketAdapter: WebSocketAdapter

    abstract suspend fun signIn()
    abstract suspend fun signIn(login: String, password: String)
    abstract suspend fun signUp(login: String, password: String)
    abstract suspend fun logout()
}