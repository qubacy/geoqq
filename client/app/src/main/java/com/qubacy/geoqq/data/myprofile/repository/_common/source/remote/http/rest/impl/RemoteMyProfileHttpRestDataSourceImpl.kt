package com.qubacy.geoqq.data.myprofile.repository._common.source.remote.http.rest.impl

import com.qubacy.geoqq.data._common.repository._common.source.remote.http._common.executor.impl.HttpCallExecutorImpl
import com.qubacy.geoqq.data.myprofile.repository._common.source.remote.http.rest._common.RemoteMyProfileHttpRestDataSource
import com.qubacy.geoqq.data.myprofile.repository._common.source.remote.http.rest._common.api.RemoteMyProfileHttpRestDataSourceApi
import com.qubacy.geoqq.data.myprofile.repository._common.source.remote.http.rest._common.api.request.UpdateMyProfileRequest
import com.qubacy.geoqq.data.myprofile.repository._common.source.remote.http.rest._common.api.response.GetMyProfileResponse
import javax.inject.Inject

class RemoteMyProfileHttpRestDataSourceImpl @Inject constructor(
    private val mRemoteMyProfileHttpRestDataSourceApi: RemoteMyProfileHttpRestDataSourceApi,
    private val mHttpCallExecutor: HttpCallExecutorImpl
) : RemoteMyProfileHttpRestDataSource {
    override fun getMyProfile(): GetMyProfileResponse {
        val getMyProfileCall = mRemoteMyProfileHttpRestDataSourceApi.getMyProfile()
        val getMyProfileResponse = mHttpCallExecutor.executeNetworkRequest(getMyProfileCall)

        return getMyProfileResponse
    }

    override fun updateMyProfile(
         updateMyProfileRequest: UpdateMyProfileRequest
    ) {
        val updateMyProfileCall = mRemoteMyProfileHttpRestDataSourceApi
            .updateMyProfile(updateMyProfileRequest)

        mHttpCallExecutor.executeNetworkRequest(updateMyProfileCall)
    }

    override fun deleteMyProfile() {
        val deleteMyProfileCall = mRemoteMyProfileHttpRestDataSourceApi.deleteMyProfile()

        mHttpCallExecutor.executeNetworkRequest(deleteMyProfileCall)
    }
}