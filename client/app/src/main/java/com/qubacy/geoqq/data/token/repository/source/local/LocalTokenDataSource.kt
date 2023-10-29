package com.qubacy.geoqq.data.token.repository.source.local

import android.content.SharedPreferences
import com.qubacy.geoqq.data.common.repository.source.DataSource

class LocalTokenDataSource(
    private val mTokenSharedPreferences: SharedPreferences
) : DataSource {
    companion object {
        const val TOKEN_SHARED_PREFERENCES_NAME = "token"

        const val REFRESH_TOKEN_PREFERENCE_KEY = "refreshToken"
    }

    private var mAccessToken: String? = null
    val accessToken: String? = mAccessToken

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

    fun checkRefreshTokenForValidity(refreshToken: String): Boolean {
        // todo: decoding the data part of the token..

        // todo: checking an expiration time..


        return true
    }
}