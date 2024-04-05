package com.qubacy.geoqq.data._common.util.http.executor._test.mock

import okhttp3.Dispatcher
import okhttp3.OkHttpClient
import org.mockito.Mockito

class OkHttpClientMockContainer {
    val httpClient: OkHttpClient

    private var mCancelAllCallFlag: Boolean = false
    val cancelAllCallFlag get() = mCancelAllCallFlag

    init {
        httpClient = mockHttpClient()
    }

    private fun mockHttpClient(): OkHttpClient {
        val dispatcherMock = Mockito.mock(Dispatcher::class.java)

        Mockito.`when`(dispatcherMock.cancelAll()).thenAnswer {
            mCancelAllCallFlag = true

            Unit
        }

        val httpClientMock = Mockito.mock(OkHttpClient::class.java)

        Mockito.`when`(httpClientMock.dispatcher()).thenAnswer {
            dispatcherMock
        }

        return httpClientMock
    }

    fun reset() {
        mCancelAllCallFlag = false
    }
}