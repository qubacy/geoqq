package com.qubacy.geoqq.data.auth.repository._common.source.remote.http.rest._common.api

import com.qubacy.geoqq.data.auth.repository._common.source.remote.http.rest._common.api.response.SignInResponse
import com.qubacy.geoqq.data.auth.repository._common.source.remote.http.rest._common.api.response.SignUpResponse
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface RemoteAuthHttpRestDataSourceApi {
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