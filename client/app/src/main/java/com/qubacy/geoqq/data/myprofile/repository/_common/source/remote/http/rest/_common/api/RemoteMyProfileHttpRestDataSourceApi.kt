package com.qubacy.geoqq.data.myprofile.repository._common.source.remote.http.rest._common.api

import com.qubacy.geoqq.data.myprofile.repository._common.source.remote.http.rest._common.api.request.UpdateMyProfileRequest
import com.qubacy.geoqq.data.myprofile.repository._common.source.remote.http.rest._common.api.response.GetMyProfileResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PUT

interface RemoteMyProfileHttpRestDataSourceApi {
    @GET("/api/my-profile")
    fun getMyProfile(): Call<GetMyProfileResponse>

    @PUT("/api/my-profile")
    fun updateMyProfile(
        @Body body: UpdateMyProfileRequest
    ): Call<Unit>

    @DELETE("/api/my-profile")
    fun deleteMyProfile(): Call<Unit>
}