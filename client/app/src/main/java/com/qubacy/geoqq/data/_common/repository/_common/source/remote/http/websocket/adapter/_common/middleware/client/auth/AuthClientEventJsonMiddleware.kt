package com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.adapter._common.middleware.client.auth

import com.qubacy.geoqq.data._common.repository._common.source.local.database.error._common.LocalErrorDatabaseDataSource
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.adapter._common.middleware.client._common.ClientEventJsonMiddleware
import com.qubacy.geoqq.data._common.repository.token.repository._common.TokenDataRepository
import com.squareup.moshi.JsonWriter
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class AuthClientEventJsonMiddleware @Inject constructor(
    private val mLocalErrorDatabaseDataSource: LocalErrorDatabaseDataSource,
    private val mTokenDataRepository: TokenDataRepository
) : ClientEventJsonMiddleware {
    companion object {
        const val ACCESS_TOKEN_PROP_NAME = "access-token"
    }

    override fun process(jsonWriter: JsonWriter): Unit = runBlocking {
        val accessToken = getAccessToken()

        jsonWriter.name(ACCESS_TOKEN_PROP_NAME)
        jsonWriter.value(accessToken)
    }

    private suspend fun getAccessToken(): String {
        return mTokenDataRepository.getTokens().accessToken
    }
}