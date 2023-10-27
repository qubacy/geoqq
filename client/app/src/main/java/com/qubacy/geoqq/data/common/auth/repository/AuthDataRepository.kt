package com.qubacy.geoqq.data.common.auth.repository

import com.qubacy.geoqq.common.repository.DataRepository
import com.qubacy.geoqq.common.repository.source.network.model.toRemoteError
import com.qubacy.geoqq.data.common.auth.operation.AuthorizeOperation
import com.qubacy.geoqq.data.common.auth.repository.error.AuthDataErrorEnum
import com.qubacy.geoqq.data.common.auth.repository.source.local.LocalAuthDataSource
import com.qubacy.geoqq.data.common.auth.repository.source.network.NetworkAuthDataSource
import com.qubacy.geoqq.data.common.auth.state.AuthState
import com.qubacy.geoqq.data.common.operation.HandleErrorOperation
import com.qubacy.geoqq.data.common.util.HasherUtil
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow

class AuthDataRepository(
    val networkAuthDataSource: NetworkAuthDataSource,
    val localAuthDataSource: LocalAuthDataSource
) : DataRepository {
    companion object {
        val DEFAULT_HASH_ALGORITHM = HasherUtil.HashAlgorithm.SHA256
    }

    private val mAuthDataFlow = MutableSharedFlow<AuthState>()
    val authDataFlow: Flow<AuthState> = mAuthDataFlow

    private suspend fun onNoLocalTokenFound() {
        val error = AuthDataErrorEnum.LOCAL_REFRESH_TOKEN_NOT_FOUND.error
        val state = AuthState(false, listOf(HandleErrorOperation(error)))

        mAuthDataFlow.emit(state)
    }

    suspend fun signInWithLocalToken() {
        val refreshToken = localAuthDataSource.loadRefreshToken()

        if (refreshToken == null) {
            onNoLocalTokenFound()

            return
        }

        // checking the token for validity:

        val checkResponse = networkAuthDataSource.checkRefreshToken(refreshToken).execute()
        val error = if (!checkResponse.isSuccessful) {
            if (checkResponse.body() != null)
                checkResponse.body()!!.error.toRemoteError()

            AuthDataErrorEnum.UNKNOWN_NETWORK_RESPONSE_ERROR.error

        } else null

        if (error != null) {
            val state = AuthState(false, listOf(HandleErrorOperation(error)))

            mAuthDataFlow.emit(state)

            return
        }
    }

    suspend fun signInWithState(
        username: String,
        password: String
    ) {
        val passwordHash = HasherUtil.hashString(password, DEFAULT_HASH_ALGORITHM)
        val response = networkAuthDataSource.signIn(username, passwordHash).execute()

        val error = if (!response.isSuccessful) {
            if (response.body() != null)
                response.body()!!.error.toRemoteError()

            AuthDataErrorEnum.UNKNOWN_NETWORK_RESPONSE_ERROR.error

        } else null

        if (error != null) {
            val state = AuthState(false, listOf(HandleErrorOperation(error)))

            mAuthDataFlow.emit(state)

            return
        }

        val authResponse = response.body()!!

        localAuthDataSource.saveTokens(authResponse.accessToken, authResponse.refreshToken)

        val authState = AuthState(true, listOf(AuthorizeOperation()))

        mAuthDataFlow.emit(authState)
    }

    suspend fun refreshAccessToken() {

    }
}