package com.qubacy.geoqq.data.myprofile.repository.source.network

import com.qubacy.geoqq.data.common.repository.common.source.DataSource
import com.qubacy.geoqq.data.myprofile.repository.source.network.model.request.UpdateMyProfileRequestBody
import com.qubacy.geoqq.data.myprofile.repository.source.network.model.response.GetMyProfileResponse
import com.qubacy.geoqq.data.myprofile.repository.source.network.model.response.UpdateMyProfileResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Query

interface NetworkMyProfileDataSource : DataSource {
    @GET("/api/my-profile")
    fun getMyProfile(@Query("accessToken") accessToken: String): Call<GetMyProfileResponse>

    @PUT("/api/my-profile")
    fun updateMyProfile(@Body body: UpdateMyProfileRequestBody): Call<UpdateMyProfileResponse>
}