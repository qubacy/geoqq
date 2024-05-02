package com.qubacy.geoqq.data.auth.repository

import com.qubacy.geoqq._common.exception.error.ErrorAppException
import com.qubacy.geoqq.data._common.repository._common.DataRepository
import com.qubacy.geoqq.data._common.util.hasher.HasherUtil
import com.qubacy.geoqq.data._common.repository._common.error.type.token.DataTokenErrorType
import com.qubacy.geoqq.data._common.repository._common.source.local.database.error.LocalErrorDataSource
import com.qubacy.geoqq.data._common.repository._common.source.local.datastore.token.LocalTokenDataStoreDataSource
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.token.HttpTokenDataSource
import com.qubacy.geoqq.data._common.repository._common.util.token.TokenUtils
import com.qubacy.geoqq.data.auth.repository.source.http.HttpAuthDataSource
import com.qubacy.geoqq.data.auth.repository.source.local.database.LocalAuthDatabaseDataSource
import javax.inject.Inject

class AuthDataRepository @Inject constructor(
    private val mErrorSource: LocalErrorDataSource,
    private val mLocalTokenDataStoreDataSource: LocalTokenDataStoreDataSource,
    private val mLocalAuthDatabaseDataSource: LocalAuthDatabaseDataSource,
    private val mHttpTokenDataSource: HttpTokenDataSource,
    private val mHttpAuthDataSource: HttpAuthDataSource
) : DataRepository {
    companion object {
        const val TAG = "AuthDataRepository"
    }

    suspend fun signIn() {
        val localRefreshToken = mLocalTokenDataStoreDataSource.getRefreshToken()

        val isRefreshTokenValid =
            if (localRefreshToken != null) TokenUtils.checkTokenForValidity(localRefreshToken)
            else false

        if (!isRefreshTokenValid)
            throw ErrorAppException(mErrorSource.getError(
                DataTokenErrorType.LOCAL_REFRESH_TOKEN_INVALID.getErrorCode()))

        val updateTokensResponse = mHttpTokenDataSource.updateTokens(localRefreshToken!!)

        mLocalTokenDataStoreDataSource.saveTokens(
            updateTokensResponse.accessToken,
            updateTokensResponse.refreshToken
        )
    }

    @OptIn(ExperimentalStdlibApi::class)
    suspend fun signIn(
        login: String,
        password: String
    ) {
        val passwordHashBytes = HasherUtil.hashString(password, HasherUtil.HashAlgorithm.SHA256)
        val passwordHash = passwordHashBytes.toHexString()

        val signInResponse = mHttpAuthDataSource.signIn(login, passwordHash)

        mLocalTokenDataStoreDataSource.saveTokens(
            signInResponse.accessToken,
            signInResponse.refreshToken
        )
    }

    @OptIn(ExperimentalStdlibApi::class)
    suspend fun signUp(
        login: String,
        password: String
    ) {
        val passwordHashBytes = HasherUtil.hashString(password, HasherUtil.HashAlgorithm.SHA256)
        val passwordHash = passwordHashBytes.toHexString()

        val signUpResponse = mHttpAuthDataSource.signUp(login, passwordHash)

        mLocalTokenDataStoreDataSource.saveTokens(
            signUpResponse.accessToken,
            signUpResponse.refreshToken
        )
    }

    suspend fun logout() {
        mLocalTokenDataStoreDataSource.clearTokens()
        mLocalAuthDatabaseDataSource.dropDataTables()
    }
}