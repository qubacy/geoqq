package com.qubacy.geoqq.data._common.repository._common.source.remote.http._common.request.middleware.auth._common._test.mock

import com.qubacy.geoqq._common._test.util.mock.AnyMockUtil
import com.qubacy.geoqq.data._common.repository._common.source.remote.http._common.request.middleware.auth._common.AuthorizationRequestMiddleware
import okhttp3.Request
import org.mockito.Mockito

class AuthorizationRequestMiddlewareMockContainer {
    val authorizationRequestMiddleware: AuthorizationRequestMiddleware

    var request: Request? = null

    private var mProcessCallFlag = false
    val processCallFlag get() = mProcessCallFlag

    init {
        authorizationRequestMiddleware = mockAuthorizationRequestMiddleware()
    }

    fun clear() {
        mProcessCallFlag = false
    }

    private fun mockAuthorizationRequestMiddleware(): AuthorizationRequestMiddleware {
        val authorizationRequestMiddlewareMock =
            Mockito.mock(AuthorizationRequestMiddleware::class.java)

        Mockito.`when`(authorizationRequestMiddlewareMock.process(
            AnyMockUtil.anyObject()
        )).thenAnswer {
            mProcessCallFlag = true
            request!!
        }

        return authorizationRequestMiddleware
    }
}