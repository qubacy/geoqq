package com.qubacy.geoqq.data._common.repository._common.source.remote.http._common.executor.mock

import com.qubacy.geoqq._common._test.util.mock.AnyMockUtil
import com.qubacy.geoqq._common.exception.error.ErrorAppException
import com.qubacy.geoqq._common.model.error._common.Error
import com.qubacy.geoqq.data._common.repository._common.source.remote.http._common.executor.impl.HttpCallExecutorImpl
import okhttp3.ResponseBody
import org.mockito.Mockito

class HttpCallExecutorMockContainer {
    val httpCallExecutor: HttpCallExecutorImpl

    var response: Any? = null
    var error: Error? = null

    private var mExecuteNetworkRequestCallFlag: Boolean = false
    val executeNetworkRequestCallFlag get() = mExecuteNetworkRequestCallFlag

    init {
        httpCallExecutor = mockHttpCallExecutor()
    }

    private fun mockHttpCallExecutor(): HttpCallExecutorImpl {
        val httpCallExecutorMock = Mockito.mock(HttpCallExecutorImpl::class.java)

        Mockito.`when`(httpCallExecutorMock.executeNetworkRequest<ResponseBody>(
            AnyMockUtil.anyObject()
        )).thenAnswer {
            mExecuteNetworkRequestCallFlag = true

            if (error != null) throw ErrorAppException(error!!)

            response
        }

        return httpCallExecutorMock
    }

    fun reset() {
        response = null
        error = null

        mExecuteNetworkRequestCallFlag = false
    }
}