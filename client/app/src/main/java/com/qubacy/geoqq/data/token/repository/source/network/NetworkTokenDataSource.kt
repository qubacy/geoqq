package com.qubacy.geoqq.data.token.repository.source.network

import com.qubacy.geoqq.data.common.repository.source.DataSource
import com.qubacy.geoqq.data.token.repository.source.network.model.response.UpdateTokensResponse
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.PUT

interface NetworkTokenDataSource : DataSource {
    @FormUrlEncoded
    @PUT("/api/sign-in")
    fun updateTokens(
        @Field("refresh-token") refreshToken: String
    ): Call<UpdateTokensResponse>
}

