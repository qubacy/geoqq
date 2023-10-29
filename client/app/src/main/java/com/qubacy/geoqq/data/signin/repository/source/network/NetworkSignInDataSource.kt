package com.qubacy.geoqq.data.signin.repository.source.network

import com.qubacy.geoqq.data.signin.repository.source.network.model.response.SignInWithUsernamePasswordResponse
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import javax.sql.DataSource

interface NetworkSignInDataSource : DataSource {
    @FormUrlEncoded
    @POST("/api/sign-in")
    fun signInWithUsernameAndPassword(
        @Field("login") login: String,
        @Field("password") passwordHash: String
    ): Call<SignInWithUsernamePasswordResponse>
}