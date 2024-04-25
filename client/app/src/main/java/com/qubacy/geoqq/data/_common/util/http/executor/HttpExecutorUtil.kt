package com.qubacy.geoqq.data._common.util.http.executor

import com.qubacy.geoqq._common.exception._common.AppException
import com.qubacy.geoqq._common.exception.error.ErrorAppException
import com.qubacy.geoqq.data._common.repository._common.source.remote._common.error.type.DataNetworkErrorType
import com.qubacy.geoqq.data.error.repository.ErrorDataRepository
import okhttp3.OkHttpClient
import retrofit2.Call

@Deprecated("Use HttpCallExecutor instead.")
fun <ResponseBodyType>executeNetworkRequest(
    errorDataRepository: ErrorDataRepository,
    httpClient: OkHttpClient,
    call: Call<ResponseBodyType>
): ResponseBodyType {
    try {
//        Log.d(
//            "HttpExecutorUtil",
//            "executeNetworkRequest(): ${call.request().method()} ${call.request().url()};"
//        )

        val response = call.execute()
        val errorBody = response.errorBody()

        if (errorBody != null) {
            val code = response.code()

            if (code in 400 until 500)
                throw IllegalStateException()
            else
                throw ErrorAppException(errorDataRepository.getError(
                    DataNetworkErrorType.RESPONSE_ERROR_WITH_SERVER_FAIL.getErrorCode()))
        }

        val responseBody = response.body()!!

        return responseBody as ResponseBodyType

    } catch (e: Throwable) {
        if (e is AppException) throw e

        e.printStackTrace()
        httpClient.dispatcher().cancelAll() // todo: is it ok?

        throw ErrorAppException(
            errorDataRepository.getError(
                DataNetworkErrorType.REQUEST_FAILED.getErrorCode()))
    }
}