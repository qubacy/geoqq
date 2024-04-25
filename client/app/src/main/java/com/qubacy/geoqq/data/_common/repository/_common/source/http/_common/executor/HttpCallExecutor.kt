package com.qubacy.geoqq.data._common.repository._common.source.http._common.executor

import com.qubacy.geoqq._common.exception._common.AppException
import com.qubacy.geoqq._common.exception.error.ErrorAppException
import com.qubacy.geoqq.data._common.error.type.NetworkErrorType
import com.qubacy.geoqq.data._common.repository._common.source.http._common.response.error.ErrorResponse
import com.qubacy.geoqq.data._common.repository._common.source.http._common.response.error.ErrorResponseContent
import com.qubacy.geoqq.data.error.repository.ErrorDataRepository
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Retrofit
import javax.inject.Inject

class HttpCallExecutor @Inject constructor(
    private val mErrorDataRepository: ErrorDataRepository,
    private val mHttpClient: OkHttpClient,
    private val mRetrofit: Retrofit
) {
    fun <ResponseBodyType>executeNetworkRequest(
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
                        NetworkErrorType.RESPONSE_ERROR_WITH_SERVER_FAIL.getErrorCode()))
            }

            val responseBody = response.body()

            if (responseBody == null)
                throw ErrorAppException(
                    mErrorDataRepository.getError(
                        NetworkErrorType.NULL_RESPONSE_BODY.getErrorCode()))

            return responseBody as ResponseBodyType

        } catch (e: Throwable) {
            if (e is AppException) throw e

            e.printStackTrace()
            mHttpClient.dispatcher().cancelAll() // todo: is it ok?

            throw ErrorAppException(
                mErrorDataRepository.getError(
                    NetworkErrorType.REQUEST_FAILED.getErrorCode()))
        }
    }

    private fun parseErrorBody(errorBody: ResponseBody): ErrorResponseContent {
        val converter = mRetrofit.responseBodyConverter<ErrorResponse>(
            ErrorResponse::class.java, arrayOf())

        return converter.convert(errorBody)!!.error
    }
}