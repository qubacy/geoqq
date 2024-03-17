package com.qubacy.geoqq.data._common.util.http.executor

import com.qubacy.geoqq._common.exception.error.ErrorAppException
import com.qubacy.geoqq.data._common.error.type.NetworkErrorType
import com.qubacy.geoqq.data.error.repository.ErrorDataRepository
import retrofit2.Call

fun <ResponseBodyType>executeNetworkRequest(
    errorDataRepository: ErrorDataRepository,
    call: Call<ResponseBodyType>
): ResponseBodyType {
    try {
        val response = call.execute()

        if (response.errorBody() != null)
            throw ErrorAppException(
                errorDataRepository.getError(
                    NetworkErrorType.RESPONSE_ERROR.getErrorCode()))

        val responseBody = response.body()

        if (responseBody == null)
            throw ErrorAppException(
                errorDataRepository.getError(
                    NetworkErrorType.NULL_RESPONSE_BODY.getErrorCode()))

        return responseBody as ResponseBodyType

    } catch (e: Exception) {
        e.printStackTrace()

        throw ErrorAppException(
            errorDataRepository.getError(
                NetworkErrorType.REQUEST_FAILED.getErrorCode()))
    }
}