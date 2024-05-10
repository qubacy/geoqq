package com.qubacy.geoqq.data._common.repository._common.source.remote.http._common.executor

import android.util.Log
import com.qubacy.geoqq._common.exception.error.ErrorAppException
import com.qubacy.geoqq.data._common.repository._common.source.local.database.error.LocalErrorDataSource
import com.qubacy.geoqq.data._common.repository._common.source.remote._common.error.type.DataNetworkErrorType
import com.qubacy.geoqq.data._common.repository._common.source.remote.http._common.response.error.ErrorResponseContent
import com.qubacy.geoqq.data._common.repository._common.source.remote.http._common.response.error.json.adapter.ErrorJsonAdapter
import okhttp3.ResponseBody
import retrofit2.Call
import javax.inject.Inject

open class HttpCallExecutor @Inject constructor(
    private val mErrorSource: LocalErrorDataSource,
    private val mErrorJsonAdapter: ErrorJsonAdapter
) {
    companion object {
        const val TAG = "HttpCallExecutor"
    }

    open fun <ResponseBodyType>executeNetworkRequest(
        call: Call<ResponseBodyType>
    ): ResponseBodyType {
        val response = call.execute()
        val errorBody = response.errorBody()

        if (errorBody != null) {
            val code = response.code()

            if (code in 400 until 500) {
                val error = parseErrorBody(errorBody)

                throw ErrorAppException(mErrorSource.getError(error.id))

            } else
                throw ErrorAppException(mErrorSource.getError(
                    DataNetworkErrorType.RESPONSE_ERROR_WITH_SERVER_FAIL.getErrorCode()))
        }

        val responseBody = response.body()!!

        return responseBody as ResponseBodyType
    }

    private fun parseErrorBody(errorBody: ResponseBody): ErrorResponseContent {
        Log.d(TAG, "parseErrorBody(): errorBody = ${errorBody.source().peek().readUtf8()};")

        val errorResponse = mErrorJsonAdapter.fromJson(errorBody.source())!!

        return errorResponse.error
    }
}