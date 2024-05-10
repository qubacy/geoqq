package com.qubacy.geoqq.data._common.repository._common.source.remote.http._common.client.event

import com.qubacy.geoqq._common.exception.error.ErrorAppException
import com.qubacy.geoqq.data._common.repository._common.source.local.database.error.LocalErrorDataSource
import com.qubacy.geoqq.data._common.repository._common.source.remote._common.error.type.DataNetworkErrorType
import okhttp3.Call
import okhttp3.EventListener
import okhttp3.OkHttpClient
import java.io.IOException
import java.util.concurrent.atomic.AtomicReference

class HttpCallFailEventListener(
    private val mErrorSource: LocalErrorDataSource,
    private val mHttpClientRef: AtomicReference<OkHttpClient>
) : EventListener() {
    override fun callFailed(call: Call, ioe: IOException) {
        onRequestError(call, ioe)
    }

    override fun responseFailed(call: Call, ioe: IOException) {
        onRequestError(call, ioe)
    }

    private fun onRequestError(call: Call, ioe: IOException) {
        mHttpClientRef.get().dispatcher().cancelAll()

        throw ErrorAppException(
            mErrorSource.getError(DataNetworkErrorType.REQUEST_FAILED.getErrorCode()))
    }
}