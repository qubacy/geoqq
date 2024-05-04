package com.qubacy.geoqq.data._common.repository._common.source.local.datastore.token

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.qubacy.geoqq.data._common.repository._common.source._common.DataSource
import kotlinx.coroutines.flow.first

val Context.tokenDataStore: DataStore<Preferences> by preferencesDataStore(
    LocalTokenDataStoreDataSource.TOKEN_DATASTORE_NAME
)

class LocalTokenDataStoreDataSource(
    private val mTokenDataStore: DataStore<Preferences>
) : DataSource {
    companion object {
        const val TAG = "LclTokenDataStoreDtSrc"

        const val TOKEN_DATASTORE_NAME = "token"

        val REFRESH_TOKEN_KEY = stringPreferencesKey("refreshToken")
        val ACCESS_TOKEN_KEY = stringPreferencesKey("accessToken")
    }

    private var mLastAccessToken: String? = null
    private var mLastRefreshToken: String? = null

    suspend fun saveTokens(
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

    suspend fun clearTokens() {
        mLastAccessToken = null
        mLastRefreshToken = null

        mTokenDataStore.edit { it.clear() }
    }

    suspend fun getRefreshToken(): String? {
        if (mLastRefreshToken == null) loadRefreshToken()

        return mLastRefreshToken
    }

    private suspend fun loadRefreshToken() {
        val preferences = mTokenDataStore.data.first()

        mLastRefreshToken = preferences[REFRESH_TOKEN_KEY]

        Log.d(TAG, "loadRefreshToken(): mLastRefreshToken = $mLastRefreshToken;")
    }

    suspend fun getAccessToken(): String? {
        if (mLastAccessToken == null) loadAccessToken()

        return mLastAccessToken
    }

    private suspend fun loadAccessToken() {
        val preferences = mTokenDataStore.data.first()

        mLastAccessToken = preferences[ACCESS_TOKEN_KEY]

        Log.d(TAG, "loadAccessToken(): mLastAccessToken = $mLastAccessToken;")
    }
}