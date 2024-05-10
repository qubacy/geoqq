package com.qubacy.geoqq.data.myprofile.repository.source.http

import com.qubacy.geoqq.data._common.repository._common.source.remote.http._common.executor.HttpCallExecutor
import com.qubacy.geoqq.data.myprofile.repository.source.http.api.HttpMyProfileDataSourceApi
import com.qubacy.geoqq.data.myprofile.repository.source.http.api.request.UpdateMyProfileRequest
import com.qubacy.geoqq.data.myprofile.repository.source.http.api.response.GetMyProfileResponse
import javax.inject.Inject

class HttpMyProfileDataSource @Inject constructor(
    private val mHttpMyProfileDataSourceApi: HttpMyProfileDataSourceApi,
    private val mHttpCallExecutor: HttpCallExecutor
) {
    fun getMyProfile(): GetMyProfileResponse {
        val getMyProfileCall = mHttpMyProfileDataSourceApi.getMyProfile()
        val getMyProfileResponse = mHttpCallExecutor.executeNetworkRequest(getMyProfileCall)

        return getMyProfileResponse
    }

    fun updateMyProfile(
         updateMyProfileRequest: UpdateMyProfileRequest
    ) {
        val updateMyProfileCall = mHttpMyProfileDataSourceApi.updateMyProfile(updateMyProfileRequest)

        mHttpCallExecutor.executeNetworkRequest(updateMyProfileCall)
    }

    fun deleteMyProfile() {
        val deleteMyProfileCall = mHttpMyProfileDataSourceApi.deleteMyProfile()

        mHttpCallExecutor.executeNetworkRequest(deleteMyProfileCall)
    }
}