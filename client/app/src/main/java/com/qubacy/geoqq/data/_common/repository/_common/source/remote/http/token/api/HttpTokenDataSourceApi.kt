package com.qubacy.geoqq.data._common.repository._common.source.remote.http.token.api

import com.qubacy.geoqq.data._common.repository._common.source._common.DataSource
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.token.api.response.UpdateTokensResponse
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.PUT

interface HttpTokenDataSourceApi : DataSource {
    @FormUrlEncoded
    @PUT("/api/sign-in")
    fun updateTokens(
        @Field("refresh-token") refreshToken: String
    ): Call<UpdateTokensResponse>
}