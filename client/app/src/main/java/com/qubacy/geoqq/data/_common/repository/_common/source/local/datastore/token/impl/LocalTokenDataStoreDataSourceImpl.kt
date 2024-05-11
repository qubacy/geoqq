package com.qubacy.geoqq.data._common.repository._common.source.local.datastore.token.impl

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.qubacy.geoqq.data._common.repository._common.source.local.datastore.token._common.LocalTokenDataStoreDataSource
import kotlinx.coroutines.flow.first

class LocalTokenDataStoreDataSourceImpl(
    private val mTokenDataStore: DataStore<Preferences>
) : LocalTokenDataStoreDataSource {
    companion object {
        const val TAG = "LclTokenDataStoreDtSrc"

        val REFRESH_TOKEN_KEY = stringPreferencesKey("refreshToken")
        val ACCESS_TOKEN_KEY = stringPreferencesKey("accessToken")
    }

    private var mLastAccessToken: String? = null
    private var mLastRefreshToken: String? = null

    override suspend fun saveTokens(
        accessToken: String,
        refreshToken: String
    ) {
        mLastAccessToken = accessToken
        mLastRefreshToken = refreshToken

        mTokenDataStore.edit {
            it[REFRESH_TOKEN_KEY] = refreshToken
            it[ACCESS_TOKEN_KEY] = accessToken
        }
    }

    override suspend fun clearTokens() {
        mLastAccessToken = null
        mLastRefreshToken = null

        mTokenDataStore.edit { it.clear() }
    }

    override suspend fun getRefreshToken(): String? {
        if (mLastRefreshToken == null) loadRefreshToken()

        return mLastRefreshToken
    }

    private suspend fun loadRefreshToken() {
        val preferences = mTokenDataStore.data.first()

        mLastRefreshToken = preferences[REFRESH_TOKEN_KEY]

        Log.d(TAG, "loadRefreshToken(): mLastRefreshToken = $mLastRefreshToken;")
    }

    override suspend fun getAccessToken(): String? {
        if (mLastAccessToken == null) loadAccessToken()

        return mLastAccessToken
    }

    private suspend fun loadAccessToken() {
        val preferences = mTokenDataStore.data.first()

        mLastAccessToken = preferences[ACCESS_TOKEN_KEY]

        Log.d(TAG, "loadAccessToken(): mLastAccessToken = $mLastAccessToken;")
    }
}