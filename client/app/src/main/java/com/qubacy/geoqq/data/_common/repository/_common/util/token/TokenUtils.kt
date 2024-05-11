package com.qubacy.geoqq.data._common.repository._common.util.token

import com.auth0.android.jwt.Claim
import com.auth0.android.jwt.JWT
import com.qubacy.geoqq._common.exception.error.ErrorAppException
import com.qubacy.geoqq.data._common.repository._common.error.type.token.DataTokenErrorType
import com.qubacy.geoqq.data._common.repository._common.source.local.database.error._common.LocalErrorDatabaseDataSource

object TokenUtils {
    const val TAG = "TokenUtils"

    fun getTokenPayload(
        token: String,
        errorSource: LocalErrorDatabaseDataSource
    ): Map<String, Claim> {
        var jwtToken: JWT? = null

        try { jwtToken = JWT(token) }
        catch (e: Exception) { e.printStackTrace() }

        if (jwtToken == null)
            throw ErrorAppException(errorSource.getError(
                DataTokenErrorType.INVALID_TOKEN_PAYLOAD.getErrorCode()))

        return jwtToken.claims
    }

    fun checkTokenForValidity(token: String): Boolean {
        var jwtToken: JWT? = null

        try { jwtToken = JWT(token) }
        catch (e: Exception) {
            e.printStackTrace()

            return false
        }

        return !jwtToken.isExpired(0) // todo: reconsider this one;
    }
}