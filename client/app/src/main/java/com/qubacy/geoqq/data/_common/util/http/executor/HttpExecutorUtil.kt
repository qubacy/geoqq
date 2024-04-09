package com.qubacy.geoqq.data._common.util.http.executor

import com.qubacy.geoqq._common.exception.error.ErrorAppException
import com.qubacy.geoqq.data._common.error.type.NetworkErrorType
import com.qubacy.geoqq.data.error.repository.ErrorDataRepository
import okhttp3.OkHttpClient
import retrofit2.Call

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

        if (response.errorBody() != null) {
            val code = response.code()

            if (code in 400 until 500)
                throw ErrorAppException(errorDataRepository.getError(
                    NetworkErrorType.RESPONSE_ERROR_WITH_CLIENT_FAIL.getErrorCode()))
            else
                throw ErrorAppException(errorDataRepository.getError(
                    NetworkErrorType.RESPONSE_ERROR_WITH_SERVER_FAIL.getErrorCode()))
        }

        val responseBody = response.body()

        if (responseBody == null)
            throw ErrorAppException(
                errorDataRepository.getError(
                    NetworkErrorType.NULL_RESPONSE_BODY.getErrorCode()))

        return responseBody as ResponseBodyType

    } catch (e: Throwable) {
        if (e is ErrorAppException) throw e

        e.printStackTrace()
        httpClient.dispatcher().cancelAll() // todo: is it ok?

        throw ErrorAppException(
            errorDataRepository.getError(
                NetworkErrorType.REQUEST_FAILED.getErrorCode()))
    }
}