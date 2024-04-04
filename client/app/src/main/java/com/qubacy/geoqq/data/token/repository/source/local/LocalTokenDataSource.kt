package com.qubacy.geoqq.data.token.repository.source.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.qubacy.geoqq.data._common.repository._common.source._common.DataSource
import kotlinx.coroutines.flow.first
import javax.inject.Inject

val Context.tokenDataStore: DataStore<Preferences> by preferencesDataStore(
    LocalTokenDataSource.TOKEN_DATASTORE_NAME)

class LocalTokenDataSource @Inject constructor(
    private val mTokenDataStore: DataStore<Preferences>
) : DataSource {
    companion object {
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
    }

    suspend fun getAccessToken(): String? {
        if (mLastAccessToken == null) loadAccessToken()

        return mLastAccessToken
    }

    private suspend fun loadAccessToken() {
        val preferences = mTokenDataStore.data.first()

        mLastAccessToken = preferences[ACCESS_TOKEN_KEY]
    }
}