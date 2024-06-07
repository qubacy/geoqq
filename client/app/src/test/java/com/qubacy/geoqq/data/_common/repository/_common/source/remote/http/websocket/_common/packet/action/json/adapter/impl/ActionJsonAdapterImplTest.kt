package com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.packet.action.json.adapter.impl

import com.qubacy.geoqq._common._test.util.mock.AnyMockUtil
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.packet.action.json.adapter._common.ActionJsonAdapter
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.socket.adapter._common.action.PackagedAction
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.socket.adapter._common.middleware.client._common.ActionJsonMiddleware
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito

class ActionJsonAdapterImplTest {
    private lateinit var mActionJsonAdapter: ActionJsonAdapterImpl
    private lateinit var mMiddleware: ActionJsonMiddleware

    private var mMiddlewareProcessCallFlag = false

    @Before
    fun setup() {
        initActionJsonAdapter()
    }

    @After
    fun clear() {
        mMiddlewareProcessCallFlag = false
    }

    private fun initActionJsonAdapter() {
        mActionJsonAdapter = ActionJsonAdapterImpl()
        mMiddleware = mockActionJsonMiddleware()
    }

    private fun mockActionJsonMiddleware(): ActionJsonMiddleware {
        val actionJsonMiddlewareMock = Mockito.mock(ActionJsonMiddleware::class.java)

        Mockito.`when`(actionJsonMiddlewareMock.process(AnyMockUtil.anyObject())).thenAnswer {
            mMiddlewareProcessCallFlag = true

            Unit
        }

        return actionJsonMiddlewareMock
    }

    @Test
    fun toJsonTest() {
        val packagedAction = PackagedAction("test", "{}")
        val expectedJsonString =
            "{\"${ActionJsonAdapter.TYPE_PROP_NAME}\":\"${packagedAction.type}\"," +
            "\"${ActionJsonAdapter.PAYLOAD_PROP_NAME}\":${packagedAction.payload}}"

        val gottenJsonString = mActionJsonAdapter.toJson(listOf(mMiddleware), packagedAction)

        Assert.assertTrue(mMiddlewareProcessCallFlag)
        Assert.assertEquals(expectedJsonString, gottenJsonString)
    }
}