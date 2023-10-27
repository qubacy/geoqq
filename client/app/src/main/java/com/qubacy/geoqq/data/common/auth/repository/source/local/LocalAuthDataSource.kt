package com.qubacy.geoqq.data.common.auth.repository.source.local

import android.content.SharedPreferences
import com.qubacy.geoqq.common.repository.source.DataSource

class LocalAuthDataSource(
    private val mAuthSharedPreferences: SharedPreferences
) : DataSource {
    companion object {
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

        mAuthSharedPreferences.edit()
            .putString(REFRESH_TOKEN_PREFERENCE_KEY, refreshToken)
            .commit()
    }

    fun loadRefreshToken(): String? {
        if (mRefreshToken == null) {
            val refreshToken = mAuthSharedPreferences.getString(REFRESH_TOKEN_PREFERENCE_KEY, null)

            if (refreshToken == null) return null

            mRefreshToken = refreshToken
        }

        return mRefreshToken
    }
}