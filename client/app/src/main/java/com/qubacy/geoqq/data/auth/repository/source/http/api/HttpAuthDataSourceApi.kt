package com.qubacy.geoqq.data.auth.repository.source.http.api

import com.qubacy.geoqq.data._common.repository._common.source._common.DataSource
import com.qubacy.geoqq.data.auth.repository.source.http.api.response.SignInResponse
import com.qubacy.geoqq.data.auth.repository.source.http.api.response.SignUpResponse
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface HttpAuthDataSourceApi : DataSource {
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