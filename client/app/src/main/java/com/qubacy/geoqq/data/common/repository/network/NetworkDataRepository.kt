package com.qubacy.geoqq.data.common.repository.network

import com.qubacy.geoqq.data.common.repository.common.DataRepository
import com.qubacy.geoqq.data.common.repository.common.source.network.model.response.common.Response
import retrofit2.Call

abstract class NetworkDataRepository(

) : DataRepository() {
    protected var mCurrentNetworkRequest: Call<Response>? = null

    override fun interrupt() {
        mCurrentNetworkRequest?.let { it.cancel() }
    }
}