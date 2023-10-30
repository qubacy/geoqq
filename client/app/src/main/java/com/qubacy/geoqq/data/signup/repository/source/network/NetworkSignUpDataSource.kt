package com.qubacy.geoqq.data.signup.repository.source.network

import com.qubacy.geoqq.data.common.repository.source.DataSource
import com.qubacy.geoqq.data.signup.repository.source.network.response.SignUpResponse
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface NetworkSignUpDataSource : DataSource {
    @FormUrlEncoded
    @POST("/api/sign-up")
    fun signUp(
        @Field("login") login: String,
        @Field("password") passwordHash: String
    ): Call<SignUpResponse>
}