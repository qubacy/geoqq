package com.qubacy.geoqq.data.auth.repository.impl

import com.qubacy.geoqq.data._common.repository._common.result.DataResult
import com.qubacy.geoqq.data._common.util.hasher.HasherUtil
import com.qubacy.geoqq.data._common.repository._common.source.local.database.error._common.LocalErrorDatabaseDataSource
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.socket.adapter._common.WebSocketAdapter
import com.qubacy.geoqq.data._common.repository.aspect.websocket.WebSocketEventDataRepository
import com.qubacy.geoqq.data._common.repository.token.repository._common.TokenDataRepository
import com.qubacy.geoqq.data.auth.repository._common.AuthDataRepository
import com.qubacy.geoqq.data.auth.repository._common.source.local.database._common.LocalAuthDatabaseDataSource
import com.qubacy.geoqq.data.auth.repository._common.source.remote.http.rest._common.RemoteAuthHttpRestDataSource
import com.qubacy.geoqq.data.auth.repository._common.source.remote.http.websocket._common.RemoteAuthHttpWebSocketDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.merge

class AuthDataRepositoryImpl(
    coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO,
    coroutineScope: CoroutineScope = CoroutineScope(coroutineDispatcher),
    private val mErrorSource: LocalErrorDatabaseDataSource,
    private val mLocalAuthDatabaseDataSource: LocalAuthDatabaseDataSource,
    private val mRemoteAuthHttpRestDataSource: RemoteAuthHttpRestDataSource,
    private val mRemoteAuthHttpWebSocketDataSource: RemoteAuthHttpWebSocketDataSource,
    private val mTokenDataRepository: TokenDataRepository,
    private val mWebSocketAdapter: WebSocketAdapter
) : AuthDataRepository(coroutineDispatcher, coroutineScope), WebSocketEventDataRepository {
    companion object {
        const val TAG = "AuthDataRepository"
    }

    override val webSocketAdapter: WebSocketAdapter get() = mWebSocketAdapter

    override val resultFlow: Flow<DataResult> = merge(
        mResultFlow,
        mRemoteAuthHttpWebSocketDataSource.eventFlow
            .mapNotNull { mapWebSocketResultToDataResult(it) }
    )

    init {
        mRemoteAuthHttpWebSocketDataSource.setWebSocketAdapter(mWebSocketAdapter)
    }

    override suspend fun signIn() {
        mTokenDataRepository.getTokens() // todo: is it enough?
        mTokenDataRepository.updateTokens()

        launchWebSocketConnection()
    }

    @OptIn(ExperimentalStdlibApi::class)
    override suspend fun signIn(
        login: String,
        password: String
    ) {
        val passwordHashBytes = HasherUtil.hashString(password, HasherUtil.HashAlgorithm.SHA256)
        val passwordHash = passwordHashBytes.toHexString()

        val signInResponse = mRemoteAuthHttpRestDataSource.signIn(login, passwordHash)

        mTokenDataRepository.saveTokens(
            signInResponse.refreshToken,
            signInResponse.accessToken
        )

        launchWebSocketConnection()
    }

    @OptIn(ExperimentalStdlibApi::class)
    override suspend fun signUp(
        login: String,
        password: String
    ) {
        val passwordHashBytes = HasherUtil.hashString(password, HasherUtil.HashAlgorithm.SHA256)
        val passwordHash = passwordHashBytes.toHexString()

        val signUpResponse = mRemoteAuthHttpRestDataSource.signUp(login, passwordHash)

        mTokenDataRepository.saveTokens(
            signUpResponse.refreshToken,
            signUpResponse.accessToken
        )

        launchWebSocketConnection()
    }

    override suspend fun logout() {
        mTokenDataRepository.reset()
        mLocalAuthDatabaseDataSource.dropDataTables()

        mWebSocketAdapter.close()
    }

    private fun launchWebSocketConnection() {
        mWebSocketAdapter.open()
    }
}