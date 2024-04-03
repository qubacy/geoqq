package com.qubacy.geoqq.data.myprofile.repository.source.http

import com.qubacy.geoqq.data._common.repository._common.source._common.DataSource
import com.qubacy.geoqq.data.myprofile.repository.source.http.request.DeleteMyProfileRequest
import com.qubacy.geoqq.data.myprofile.repository.source.http.request.UpdateMyProfileRequest
import com.qubacy.geoqq.data.myprofile.repository.source.http.response.DeleteMyProfileResponse
import com.qubacy.geoqq.data.myprofile.repository.source.http.response.GetMyProfileResponse
import com.qubacy.geoqq.data.myprofile.repository.source.http.response.UpdateMyProfileResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.PUT
import retrofit2.http.Query

interface HttpMyProfileDataSource : DataSource {
    @GET("/api/my-profile")
    fun getMyProfile(
        @Query("accessToken") accessToken: String
    ): Call<GetMyProfileResponse>

    @PUT("/api/my-profile")
    fun updateMyProfile(
        @Body body: UpdateMyProfileRequest
    ): Call<UpdateMyProfileResponse>

    @HTTP(method = "DELETE", path = "/api/my-profile", hasBody = true)
    fun deleteMyProfile(
        @Body body: DeleteMyProfileRequest
    ): Call<DeleteMyProfileResponse>
}