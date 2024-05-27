package com.qubacy.geoqq.data.auth.repository._common

import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.socket.adapter._common.WebSocketAdapter

interface AuthDataRepository {
    val webSocketAdapter: WebSocketAdapter

    suspend fun signIn()
    suspend fun signIn(login: String, password: String)
    suspend fun signUp(login: String, password: String)
    suspend fun logout()
}