package com.qubacy.geoqq.data.common.repository.network.common

import com.qubacy.geoqq.common.error.ErrorContext
import com.qubacy.geoqq.data.common.repository.common.DataRepository
import com.qubacy.geoqq.data.common.repository.common.result.common.Result
import com.qubacy.geoqq.data.common.repository.common.result.error.ErrorResult
import com.qubacy.geoqq.data.common.repository.common.result.interruption.InterruptionResult
import com.qubacy.geoqq.data.common.repository.common.source.network.NetworkDataSourceContext
import com.qubacy.geoqq.data.common.repository.common.source.network.model.response.common.Response
import com.qubacy.geoqq.data.common.repository.network.common.result.ExecuteNetworkRequestResult
import retrofit2.Call
import java.io.IOException

abstract class NetworkDataRepository(

) : DataRepository() {
    protected var mCurrentNetworkRequest: Call<Response>? = null

    override fun interrupt() {
        mCurrentNetworkRequest?.let { it.cancel() }
    }

    private fun retrieveNetworkError(
        response: retrofit2.Response<Response>
    ): Long? {
        if (response.isSuccessful) return null
        if (response.errorBody() == null)
            return ErrorContext.Network.UNKNOWN_NETWORK_RESPONSE_ERROR.id

        val errorResponseString = response.errorBody()!!.string()
        val errorResponse = NetworkDataSourceContext
            .errorResponseJsonAdapter.fromJson(errorResponseString)

        if (errorResponse == null)
            return ErrorContext.Network.UNKNOWN_NETWORK_RESPONSE_ERROR.id

        return errorResponse.error.id
    }

    protected open fun executeNetworkRequest(call: Call<Response>): Result {
        try {
            mCurrentNetworkRequest = call
            val response = mCurrentNetworkRequest!!.execute()

            val error = retrieveNetworkError(response)

            if (error != null) return ErrorResult(error)

            return ExecuteNetworkRequestResult(response.body() as Response)

        } catch (e: IOException) {
            if (mCurrentNetworkRequest!!.isCanceled) return InterruptionResult()

            return ErrorResult(ErrorContext.Network.UNKNOWN_NETWORK_FAILURE.id)
        }
    }
}