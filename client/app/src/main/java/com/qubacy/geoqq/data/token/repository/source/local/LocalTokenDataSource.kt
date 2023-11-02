package com.qubacy.geoqq.data.token.repository.source.local

import android.content.SharedPreferences
import com.auth0.android.jwt.JWT
import com.qubacy.geoqq.data.common.repository.common.source.DataSource
import java.lang.Exception

class LocalTokenDataSource(
    private val mTokenSharedPreferences: SharedPreferences
) : DataSource {
    companion object {
        const val TOKEN_SHARED_PREFERENCES_NAME = "token"

        const val REFRESH_TOKEN_PREFERENCE_KEY = "refreshToken"
    }

    private var mAccessToken: String? = null
    val accessToken: String? get() { return mAccessToken }

    private var mRefreshToken: String? = null

    fun saveTokens(
        accessToken: String,
        refreshToken: String
    ) {
        mAccessToken = accessToken
        mRefreshToken = refreshToken

        mTokenSharedPreferences.edit()
            .putString(REFRESH_TOKEN_PREFERENCE_KEY, refreshToken)
            .commit()
    }

    fun loadRefreshToken(): String? {
        if (mRefreshToken == null) {
            val refreshToken = mTokenSharedPreferences.getString(REFRESH_TOKEN_PREFERENCE_KEY, null)

            if (refreshToken == null) return null

            mRefreshToken = refreshToken
        }

        return mRefreshToken
    }

    fun checkTokenForValidity(token: String): Boolean {
        var jwtToken: JWT? = null

        try { jwtToken = JWT(token) }
        catch (e: Exception) {
            return false
        }

        return !jwtToken.isExpired(0)
    }
}