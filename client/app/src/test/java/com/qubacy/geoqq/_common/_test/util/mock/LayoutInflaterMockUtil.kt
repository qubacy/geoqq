package com.qubacy.geoqq._common._test.util.mock

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.mockito.Mockito

object LayoutInflaterMockUtil {
    @Volatile
    private var mViewMock: View? = null

    private fun mockLayoutInflater() {
        Mockito.mockStatic(LayoutInflater::class.java)

        val layoutInflaterMock = Mockito.mock(LayoutInflater::class.java)

        Mockito.`when`(layoutInflaterMock.inflate(
            Mockito.anyInt(), AnyMockUtil.anyObject<ViewGroup>(), Mockito.anyBoolean())
        ).thenAnswer { mViewMock }

        Mockito.`when`<LayoutInflater>(LayoutInflater.from(AnyMockUtil.anyObject<Context>()))
            .thenReturn(layoutInflaterMock)
    }

    fun getMockedLayoutInflater(viewMock: View): LayoutInflater {
        mViewMock = viewMock

        val contextMock = Mockito.mock(Context::class.java)
        val layoutInflaterMock =
            try { LayoutInflater.from(contextMock) }
            catch (e: Exception) {
                mockLayoutInflater()
                LayoutInflater.from(contextMock)
            }

        return layoutInflaterMock
    }
}