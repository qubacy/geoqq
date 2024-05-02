package com.qubacy.geoqq.data.myprofile.repository.source.http.api

import com.qubacy.geoqq.data._common.repository._common.source._common.DataSource
import com.qubacy.geoqq.data.myprofile.repository.source.http.api.request.UpdateMyProfileRequest
import com.qubacy.geoqq.data.myprofile.repository.source.http.api.response.GetMyProfileResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PUT

interface HttpMyProfileDataSourceApi : DataSource {
    @GET("/api/my-profile")
    fun getMyProfile(): Call<GetMyProfileResponse>

    @PUT("/api/my-profile")
    fun updateMyProfile(
        @Body body: UpdateMyProfileRequest
    ): Call<Unit>

    @DELETE("/api/my-profile")
    fun deleteMyProfile(): Call<Unit>
}