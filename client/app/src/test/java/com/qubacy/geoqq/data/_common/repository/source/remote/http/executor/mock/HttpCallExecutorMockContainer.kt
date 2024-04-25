package com.qubacy.geoqq.data._common.repository.source.remote.http.executor.mock

import com.qubacy.geoqq._common._test.util.mock.AnyMockUtil
import com.qubacy.geoqq._common.exception.error.ErrorAppException
import com.qubacy.geoqq._common.model.error._common.Error
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.executor.HttpCallExecutor
import okhttp3.ResponseBody
import org.mockito.Mockito

class HttpCallExecutorMockContainer {
    val httpCallExecutor: HttpCallExecutor

    var response: Any? = null
    var error: Error? = null

    private var mExecuteNetworkRequestCallFlag: Boolean = false
    val executeNetworkRequestCallFlag get() = mExecuteNetworkRequestCallFlag

    init {
        httpCallExecutor = mockHttpCallExecutor()
    }

    private fun mockHttpCallExecutor(): HttpCallExecutor {
        val httpCallExecutorMock = Mockito.mock(HttpCallExecutor::class.java)

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