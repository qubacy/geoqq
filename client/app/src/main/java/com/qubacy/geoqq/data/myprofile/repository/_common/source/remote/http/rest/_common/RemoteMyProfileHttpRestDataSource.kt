package com.qubacy.geoqq.data.myprofile.repository._common.source.remote.http.rest._common

import com.qubacy.geoqq.data.myprofile.repository._common.source.remote.http.rest._common.api.request.UpdateMyProfileRequest
import com.qubacy.geoqq.data.myprofile.repository._common.source.remote.http.rest._common.api.response.GetMyProfileResponse

interface RemoteMyProfileHttpRestDataSource {
    fun getMyProfile(): GetMyProfileResponse
    fun updateMyProfile(updateMyProfileRequest: UpdateMyProfileRequest)
    fun deleteMyProfile()
}