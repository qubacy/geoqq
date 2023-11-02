package com.qubacy.geoqq.data.common.repository.network

import com.qubacy.geoqq.data.common.repository.common.DataRepository
import com.qubacy.geoqq.data.common.repository.common.result.common.Result
import com.qubacy.geoqq.data.common.repository.common.result.error.ErrorResult
import com.qubacy.geoqq.data.common.repository.common.result.interruption.InterruptionResult
import com.qubacy.geoqq.data.common.repository.common.source.network.error.NetworkDataSourceErrorEnum
import com.qubacy.geoqq.data.common.repository.common.source.network.model.response.common.Response
import com.qubacy.geoqq.data.common.repository.network.result.ExecuteNetworkRequestResult
import retrofit2.Call
import java.io.IOException

abstract class NetworkDataRepository(

) : DataRepository() {
    protected var mCurrentNetworkRequest: Call<Response>? = null

    override fun interrupt() {
        mCurrentNetworkRequest?.let { it.cancel() }
    }

    protected open fun executeNetworkRequest(call: Call<Response>): Result {
        try {
            mCurrentNetworkRequest = call
            val response = mCurrentNetworkRequest!!.execute()

            return ExecuteNetworkRequestResult(response)

        } catch (e: IOException) {
            if (mCurrentNetworkRequest!!.isCanceled) return InterruptionResult()

            return ErrorResult(NetworkDataSourceErrorEnum.UNKNOWN_NETWORK_FAILURE.error)
        }
    }
}