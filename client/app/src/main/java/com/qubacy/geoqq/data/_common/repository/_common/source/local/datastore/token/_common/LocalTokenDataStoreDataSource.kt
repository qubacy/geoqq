package com.qubacy.geoqq.data._common.repository._common.source.local.datastore.token._common

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore

val Context.tokenDataStore: DataStore<Preferences> by preferencesDataStore(
    LocalTokenDataStoreDataSource.TOKEN_DATASTORE_NAME
)

interface LocalTokenDataStoreDataSource {
    companion object {
        const val TOKEN_DATASTORE_NAME = "token"

        val REFRESH_TOKEN_KEY = stringPreferencesKey("refreshToken")
        val ACCESS_TOKEN_KEY = stringPreferencesKey("accessToken")
    }

    suspend fun saveTokens(accessToken: String, refreshToken: String)
    suspend fun clearTokens()
    suspend fun getRefreshToken(): String?
    suspend fun getAccessToken(): String?
}