package com.qubacy.geoqq.data._common.repository._common.source.remote.http.executor

import com.qubacy.geoqq._common.exception._common.AppException
import com.qubacy.geoqq._common.exception.error.ErrorAppException
import com.qubacy.geoqq.data._common.repository._common.source.remote._common.error.type.DataNetworkErrorType
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.response.error.ErrorResponse
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.response.error.ErrorResponseContent
import com.qubacy.geoqq.data.error.repository.ErrorDataRepository
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Retrofit
import javax.inject.Inject

open class HttpCallExecutor @Inject constructor(
    private val mErrorDataRepository: ErrorDataRepository,
    private val mHttpClient: OkHttpClient,
    private val mRetrofit: Retrofit
) {
    open fun <ResponseBodyType>executeNetworkRequest(
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

                if (code in 400 until 500) {
                    val error = parseErrorBody(errorBody)

                    throw ErrorAppException(mErrorDataRepository.getError(error.id))

                } else
                    throw ErrorAppException(mErrorDataRepository.getError(
                        DataNetworkErrorType.RESPONSE_ERROR_WITH_SERVER_FAIL.getErrorCode()))
            }

            val responseBody = response.body()!!

            return responseBody as ResponseBodyType

        } catch (e: Throwable) {
            if (e is AppException) throw e

            e.printStackTrace()
            mHttpClient.dispatcher().cancelAll() // todo: is it ok?

            throw ErrorAppException(
                mErrorDataRepository.getError(
                    DataNetworkErrorType.REQUEST_FAILED.getErrorCode()))
        }
    }

    private fun parseErrorBody(errorBody: ResponseBody): ErrorResponseContent {
        val converter = mRetrofit.responseBodyConverter<ErrorResponse>(
            ErrorResponse::class.java, arrayOf())

        return converter.convert(errorBody)!!.error
    }
}