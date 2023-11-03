package com.qubacy.geoqq.data.common.repository.common

import com.qubacy.geoqq.common.error.ErrorContext
import com.qubacy.geoqq.data.common.repository.common.source.network.NetworkDataSourceContext
import com.qubacy.geoqq.data.common.repository.common.source.network.model.response.common.Response
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

abstract class DataRepository(
    val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    protected fun retrieveNetworkError(
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

    abstract fun interrupt()
}