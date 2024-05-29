package com.qubacy.geoqq.data.auth.repository.impl

import com.qubacy.geoqq.data._common.util.hasher.HasherUtil
import com.qubacy.geoqq.data._common.repository._common.source.local.database.error._common.LocalErrorDatabaseDataSource
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.socket.adapter._common.WebSocketAdapter
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.socket.adapter._di.module.WebSocketAdapterCreateQualifier
import com.qubacy.geoqq.data._common.repository.token.repository._common.TokenDataRepository
import com.qubacy.geoqq.data.auth.repository._common.AuthDataRepository
import com.qubacy.geoqq.data.auth.repository._common.source.local.database._common.LocalAuthDatabaseDataSource
import com.qubacy.geoqq.data.auth.repository._common.source.remote.http.rest._common.RemoteAuthHttpRestDataSource
import javax.inject.Inject

class AuthDataRepositoryImpl @Inject constructor(
    private val mErrorSource: LocalErrorDatabaseDataSource,
    private val mLocalAuthDatabaseDataSource: LocalAuthDatabaseDataSource,
    private val mRemoteAuthHttpRestDataSource: RemoteAuthHttpRestDataSource,
    private val mTokenDataRepository: TokenDataRepository,
    @WebSocketAdapterCreateQualifier private val mWebSocketAdapter: WebSocketAdapter
) : AuthDataRepository {
    companion object {
        const val TAG = "AuthDataRepository"
    }

    override val webSocketAdapter: WebSocketAdapter get() = mWebSocketAdapter

    override suspend fun signIn() {
        mTokenDataRepository.getTokens() // todo: is it enough?
        mTokenDataRepository.updateTokens()
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
            signInResponse.accessToken,
            signInResponse.refreshToken
        )
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
            signUpResponse.accessToken,
            signUpResponse.refreshToken
        )
    }

    override suspend fun logout() {
        mTokenDataRepository.reset()
        mLocalAuthDatabaseDataSource.dropDataTables()

        mWebSocketAdapter.close()
    }
}