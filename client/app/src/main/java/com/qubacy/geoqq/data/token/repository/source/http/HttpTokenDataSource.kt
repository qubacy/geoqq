package com.qubacy.geoqq.data.token.repository.source.http

import com.qubacy.geoqq.data._common.repository._common.source._common.DataSource
import com.qubacy.geoqq.data.token.repository.source.http.response.SignInResponse
import com.qubacy.geoqq.data.token.repository.source.http.response.SignUpResponse
import com.qubacy.geoqq.data.token.repository.source.http.response.UpdateTokensResponse
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import retrofit2.http.PUT

interface HttpTokenDataSource : DataSource {
    @FormUrlEncoded
    @PUT("/api/sign-in")
    fun updateTokens(
        @Field("refresh-token") refreshToken: String
    ): Call<UpdateTokensResponse>

    @FormUrlEncoded
    @POST("/api/sign-in")
    fun signIn(
        @Field("login") login: String,
        @Field("password") passwordHash: String
    ): Call<SignInResponse>

    @FormUrlEncoded
    @POST("/api/sign-up")
    fun signUp(
        @Field("login") login: String,
        @Field("password") passwordHash: String
    ): Call<SignUpResponse>
}