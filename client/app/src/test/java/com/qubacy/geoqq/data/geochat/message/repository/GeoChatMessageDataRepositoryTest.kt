package com.qubacy.geoqq.data.geochat.message.repository

import com.qubacy.geoqq.data.common.repository.common.result.common.Result
import com.qubacy.geoqq.data.common.repository.message.MessageDataRepositoryTest
import com.qubacy.geoqq.data.common.repository.message.result.GetMessagesResult
import com.qubacy.geoqq.data.common.repository.network.NetworkTestContext
import com.qubacy.geoqq.data.geochat.message.repository.source.network.model.NetworkGeoMessageDataSource
import com.qubacy.geoqq.data.geochat.message.repository.source.websocket.WebSocketUpdateGeoMessageDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import java.util.concurrent.atomic.AtomicReference

class GeoChatMessageDataRepositoryTest : MessageDataRepositoryTest() {
    private lateinit var mGeoMessageDataRepository: GeoMessageDataRepository
    private lateinit var mResultListAtomicRef: AtomicReference<List<Result>>

    private fun initDataRepository(
        code: Int = 200,
        networkResponseMessageCount: Int = 0
    ) {
        val networkResponseString = generateNetworkResponseWithCount(networkResponseMessageCount)
        val networkMateMessageDataSource = NetworkTestContext.generateTestRetrofit(
            NetworkTestContext.generateDefaultTestInterceptor(code, networkResponseString)
        ).create(NetworkGeoMessageDataSource::class.java)

        val webSocketUpdateMateMessageDataSource = Mockito.mock(
            WebSocketUpdateGeoMessageDataSource::class.java)

        Mockito.`when`(webSocketUpdateMateMessageDataSource.updateFlow)
            .thenReturn(MutableSharedFlow())

        mGeoMessageDataRepository = GeoMessageDataRepository(
            networkMateMessageDataSource,
            webSocketUpdateMateMessageDataSource
        )

        mResultListAtomicRef = AtomicReference<List<Result>>(listOf())
        GlobalScope.launch(Dispatchers.IO) {
            mGeoMessageDataRepository.resultFlow.collect {
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
    fun getMateChatFromNetworkTest() {
        val geoMessageCount = 2

        val radius = 0
        val latitude = 0.0
        val longitude = 0.0

        initDataRepository(networkResponseMessageCount = geoMessageCount)

        val job = GlobalScope.launch {
            mGeoMessageDataRepository.getGeoMessages(radius, latitude, longitude, String())
        }

        while (mResultListAtomicRef.get().isEmpty()) { }

        val result = mResultListAtomicRef.get().last()

        Assert.assertEquals(result::class, GetMessagesResult::class)

        val resultCast = result as GetMessagesResult

        Assert.assertEquals(geoMessageCount, resultCast.messages.size)

        job.cancel()
    }
}