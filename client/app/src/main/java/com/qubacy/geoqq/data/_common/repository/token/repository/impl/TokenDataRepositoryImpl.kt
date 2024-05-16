package com.qubacy.geoqq.data._common.repository.token.repository.impl

import com.qubacy.geoqq._common.exception.error.ErrorAppException
import com.qubacy.geoqq.data._common.repository._common.source.local.database.error._common.LocalErrorDatabaseDataSource
import com.qubacy.geoqq.data._common.repository.token.error.type.DataTokenErrorType
import com.qubacy.geoqq.data._common.repository.token.repository._common.source.local.datastore._common.LocalTokenDataStoreDataSource
import com.qubacy.geoqq.data._common.repository.token.repository._common.source.remote.http.rest._common.RemoteTokenHttpRestDataSource
import com.qubacy.geoqq.data._common.repository.token.repository._common.TokenDataRepository
import com.qubacy.geoqq.data._common.repository.token.repository._common.result.get.GetTokensDataResult
import com.qubacy.geoqq.data._common.repository.token.repository._common.result.update.UpdateTokensDataResult
import com.qubacy.geoqq.data._common.repository.token.repository._common.util.TokenUtils
import javax.inject.Inject

class TokenDataRepositoryImpl @Inject constructor(
    private val mLocalErrorDatabaseDataSource: LocalErrorDatabaseDataSource,
    private val mLocalTokenDataStoreDataSource: LocalTokenDataStoreDataSource,
    private val mRemoteTokenHttpRestDataSource: RemoteTokenHttpRestDataSource
) : TokenDataRepository {
    override suspend fun getTokens(): GetTokensDataResult {
        val localAccessToken = mLocalTokenDataStoreDataSource.getAccessToken()
        val localRefreshToken = mLocalTokenDataStoreDataSource.getRefreshToken()

        val isRefreshTokenValid =
            if (localRefreshToken != null) TokenUtils.checkTokenForValidity(localRefreshToken)
            else false

        if (!isRefreshTokenValid)
            throw ErrorAppException(mLocalErrorDatabaseDataSource.getError(
                DataTokenErrorType.LOCAL_REFRESH_TOKEN_INVALID.getErrorCode()))

        val isLocalAccessTokenValid =
            if (localAccessToken != null) TokenUtils.checkTokenForValidity(localAccessToken)
            else false

        if (isLocalAccessTokenValid)
            return GetTokensDataResult(localRefreshToken!!, localAccessToken!!)

        val updateTokensResult = updateTokens()

        return GetTokensDataResult(
            updateTokensResult.refreshToken,
            updateTokensResult.accessToken
        )
    }

    override suspend fun updateTokens(): UpdateTokensDataResult {
        val refreshToken = mLocalTokenDataStoreDataSource.getRefreshToken()!!
        val updateTokensResponse = mRemoteTokenHttpRestDataSource.updateTokens(refreshToken)

        mLocalTokenDataStoreDataSource.saveTokens(
            updateTokensResponse.accessToken,
            updateTokensResponse.refreshToken
        )

        return UpdateTokensDataResult(
            updateTokensResponse.refreshToken,
            updateTokensResponse.accessToken
        )
    }

    override suspend fun saveTokens(
        refreshToken: String,
        accessToken: String
    ) {
        mLocalTokenDataStoreDataSource.saveTokens(accessToken, refreshToken)
    }

    override suspend fun reset() {
        mLocalTokenDataStoreDataSource.clearTokens()
    }
}