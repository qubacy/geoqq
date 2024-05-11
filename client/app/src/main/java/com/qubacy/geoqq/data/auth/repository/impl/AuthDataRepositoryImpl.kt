package com.qubacy.geoqq.data.auth.repository.impl

import com.qubacy.geoqq._common.exception.error.ErrorAppException
import com.qubacy.geoqq.data._common.util.hasher.HasherUtil
import com.qubacy.geoqq.data._common.repository._common.error.type.token.DataTokenErrorType
import com.qubacy.geoqq.data._common.repository._common.source.local.database.error._common.LocalErrorDatabaseDataSource
import com.qubacy.geoqq.data._common.repository._common.source.local.datastore.token._common.LocalTokenDataStoreDataSource
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.rest.token._common.RemoteTokenHttpRestDataSource
import com.qubacy.geoqq.data._common.repository._common.util.token.TokenUtils
import com.qubacy.geoqq.data.auth.repository._common.AuthDataRepository
import com.qubacy.geoqq.data.auth.repository._common.source.local.database._common.LocalAuthDatabaseDataSource
import com.qubacy.geoqq.data.auth.repository._common.source.remote.http.rest._common.RemoteAuthHttpRestDataSource
import javax.inject.Inject

class AuthDataRepositoryImpl @Inject constructor(
    private val mErrorSource: LocalErrorDatabaseDataSource,
    private val mLocalTokenDataStoreDataSource: LocalTokenDataStoreDataSource,
    private val mLocalAuthDatabaseDataSource: LocalAuthDatabaseDataSource,
    private val mRemoteTokenHttpRestDataSource: RemoteTokenHttpRestDataSource,
    private val mRemoteAuthHttpRestDataSource: RemoteAuthHttpRestDataSource
) : AuthDataRepository {
    companion object {
        const val TAG = "AuthDataRepository"
    }

    override suspend fun signIn() {
        val localRefreshToken = mLocalTokenDataStoreDataSource.getRefreshToken()

        val isRefreshTokenValid =
            if (localRefreshToken != null) TokenUtils.checkTokenForValidity(localRefreshToken)
            else false

        if (!isRefreshTokenValid)
            throw ErrorAppException(mErrorSource.getError(
                DataTokenErrorType.LOCAL_REFRESH_TOKEN_INVALID.getErrorCode()))

        val updateTokensResponse = mRemoteTokenHttpRestDataSource.updateTokens(localRefreshToken!!)

        mLocalTokenDataStoreDataSource.saveTokens(
            updateTokensResponse.accessToken,
            updateTokensResponse.refreshToken
        )
    }

    @OptIn(ExperimentalStdlibApi::class)
    override suspend fun signIn(
        login: String,
        password: String
    ) {
        val passwordHashBytes = HasherUtil.hashString(password, HasherUtil.HashAlgorithm.SHA256)
        val passwordHash = passwordHashBytes.toHexString()

        val signInResponse = mRemoteAuthHttpRestDataSource.signIn(login, passwordHash)

        mLocalTokenDataStoreDataSource.saveTokens(
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

        mLocalTokenDataStoreDataSource.saveTokens(
            signUpResponse.accessToken,
            signUpResponse.refreshToken
        )
    }

    override suspend fun logout() {
        mLocalTokenDataStoreDataSource.clearTokens()
        mLocalAuthDatabaseDataSource.dropDataTables()
    }
}