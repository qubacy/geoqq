package com.qubacy.geoqq.data.common.repository

import com.qubacy.geoqq.common.error.common.TypedErrorBase
import com.qubacy.geoqq.data.common.repository.source.network.NetworkDataSourceContext
import com.qubacy.geoqq.data.common.repository.source.network.error.NetworkDataSourceErrorEnum
import com.qubacy.geoqq.data.common.repository.source.network.error.toRemoteError
import com.qubacy.geoqq.data.common.repository.source.network.model.response.common.Response
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

abstract class DataRepository(
    val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    protected fun retrieveNetworkError(
        response: retrofit2.Response<Response>
    ): TypedErrorBase? {
        if (response.isSuccessful) return null
        if (response.errorBody() == null)
            return NetworkDataSourceErrorEnum.UNKNOWN_NETWORK_RESPONSE_ERROR.error

        val errorResponseString = response.errorBody()!!.string()
        val errorResponse = NetworkDataSourceContext
            .errorResponseJsonAdapter.fromJson(errorResponseString)

        if (errorResponse == null)
            return NetworkDataSourceErrorEnum.UNKNOWN_NETWORK_RESPONSE_ERROR.error

        return errorResponse.error.toRemoteError()
    }
}