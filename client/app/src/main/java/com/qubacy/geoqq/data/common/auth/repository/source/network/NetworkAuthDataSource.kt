package com.qubacy.geoqq.data.common.auth.repository.source.network

import com.qubacy.geoqq.common.repository.source.DataSource
import com.qubacy.geoqq.data.common.auth.repository.source.network.model.AuthResponse
import com.qubacy.geoqq.data.common.auth.repository.source.network.model.RefreshTokenCheckResponse
import retrofit2.Call

interface NetworkAuthDataSource : DataSource {
    fun signIn(username: String, passwordHash: String): Call<AuthResponse>
    fun signUp(username: String, passwordHash: String): Call<AuthResponse>

    fun checkRefreshToken(refreshToken: String): Call<RefreshTokenCheckResponse>
}