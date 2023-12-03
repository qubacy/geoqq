package com.qubacy.geoqq.data.mate.request.repository

import com.qubacy.geoqq.data.common.repository.common.result.common.Result
import com.qubacy.geoqq.data.common.repository.network.NetworkTestContext
import com.qubacy.geoqq.data.mate.request.repository.result.GetMateRequestsResult
import com.qubacy.geoqq.data.mate.request.repository.source.network.NetworkMateRequestDataSource
import com.qubacy.geoqq.data.mate.request.repository.source.websocket.WebSocketMateRequestDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import java.util.concurrent.atomic.AtomicReference

class MateRequestDataRepositoryTest(

) {
    private lateinit var mMateRequestDataRepository: MateRequestDataRepository
    private lateinit var mResultListAtomicRef: AtomicReference<List<Result>>

    private fun generateNetworkResponseWithCount(count: Int): String {
        val responseStringBuilder = StringBuilder("{\"requests\":[")

        for (i in 0 until count)  {
            responseStringBuilder
                .append("{\"id\":$i, \"user-id\":$i}")
            responseStringBuilder.append(if (i != count - 1) "," else "")
        }

        return responseStringBuilder.append("]}").toString()
    }

    private fun initDataRepository(
        code: Int = 200,
        networkResponseRequestCount: Int = 0
    ) {
        val networkResponseString = generateNetworkResponseWithCount(networkResponseRequestCount)
        val networkMateRequestDataSource = NetworkTestContext.generateTestRetrofit(
            NetworkTestContext.generateDefaultTestInterceptor(code, networkResponseString)
        ).create(NetworkMateRequestDataSource::class.java)

        val webSocketUpdateMateRequestDataSource = Mockito.mock(
            WebSocketMateRequestDataSource::class.java)

        Mockito.`when`(webSocketUpdateMateRequestDataSource.updateFlow)
            .thenReturn(MutableSharedFlow())

        mMateRequestDataRepository = MateRequestDataRepository(
            networkMateRequestDataSource, webSocketUpdateMateRequestDataSource
        )

        mResultListAtomicRef = AtomicReference<List<Result>>(listOf())
        GlobalScope.launch(Dispatchers.IO) {
            mMateRequestDataRepository.resultFlow.collect {
                val curList = mResultListAtomicRef.get()
                val newList = mutableListOf<Result>().apply {
                    addAll(curList)
                    add(it)
                }

                mResultListAtomicRef.set(newList)
            }
        }
    }

    @Before
    fun setup() {

    }

    @Test
    fun getMateRequestFromNetworkTest() {
        val count = 2

        initDataRepository(networkResponseRequestCount = count)

        val job = GlobalScope.launch {
            mMateRequestDataRepository.getMateRequests(String(), count, 0, false)
        }

        while (mResultListAtomicRef.get().isEmpty()) { }

        val result = mResultListAtomicRef.get().last()

        Assert.assertEquals(result::class, GetMateRequestsResult::class)

        val resultCast = result as GetMateRequestsResult

        Assert.assertEquals(count, resultCast.mateRequests.size)

        job.cancel()
    }
}