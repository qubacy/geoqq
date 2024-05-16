package com.qubacy.geoqq.data._common.repository.token.repository._common.source.remote.http.rest._common.api

import com.qubacy.geoqq.data._common.repository.token.repository._common.source.remote.http.rest._common.api.response.UpdateTokensResponse
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.PUT

interface RemoteTokenHttpRestDataSourceApi {
    @FormUrlEncoded
    @PUT("/api/sign-in")
    fun updateTokens(
        @Field("refresh-token") refreshToken: String
    ): Call<UpdateTokensResponse>
}