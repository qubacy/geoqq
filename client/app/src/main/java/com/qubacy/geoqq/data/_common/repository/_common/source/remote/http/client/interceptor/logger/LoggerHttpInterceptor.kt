package com.qubacy.geoqq.data._common.repository._common.source.remote.http.client.interceptor.logger

import android.util.Log
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.api.HttpApi
import okhttp3.Interceptor
import okhttp3.Response

class LoggerHttpInterceptor : Interceptor {
    companion object {
        const val TAG = "LoggerHttpInterceptor"
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val requestUrl = request.url()
        val requestQuery = requestUrl.query()
        val requestPathQuery = requestUrl.encodedPath() +
                if (requestQuery == null) "" else "?$requestQuery"

        val response = chain.proceed(request)
        val responseBodyChunk = response.peekBody(256)

        Log.d(
            TAG,
            "request.path?query = ${request.method()} ${requestPathQuery}; " +
                "response.body = ${responseBodyChunk.string()};"
        )

        return response
    }
}