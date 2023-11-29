package com.qubacy.geoqq.data.mate.message.repository

import com.qubacy.geoqq.common.util.mock.AnyUtility
import com.qubacy.geoqq.data.common.repository.common.result.common.Result
import com.qubacy.geoqq.data.common.repository.network.NetworkTestContext
import com.qubacy.geoqq.data.mate.message.repository.result.GetMessagesResult
import com.qubacy.geoqq.data.mate.message.repository.source.local.LocalMateMessageDataSource
import com.qubacy.geoqq.data.mate.message.repository.source.local.model.MateMessageEntity
import com.qubacy.geoqq.data.mate.message.repository.source.network.NetworkMateMessageDataSource
import com.qubacy.geoqq.data.mate.message.repository.source.websocket.WebSocketUpdateMateMessageDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import java.util.concurrent.atomic.AtomicReference

class MateMessageDataRepositoryTest() {
    private lateinit var mMateMessageDataRepository: MateMessageDataRepository
    private lateinit var mResultListAtomicRef: AtomicReference<List<Result>>

    private fun generateNetworkResponseWithCount(count: Int): String {
        val responseStringBuilder = StringBuilder("{\"messages\":[")

        for (i in 0 until count)  {
            responseStringBuilder
                .append("{\"id\":$i, \"user-id\":$i, \"text\":\"test\", \"time\":100}")
            responseStringBuilder.append(if (i != count - 1) "," else "")
        }

        return responseStringBuilder.append("]}").toString()
    }

    private fun initDataRepository(
        code: Int = 200,
        networkResponseMessageCount: Int = 0,
        mateMessageModels: List<MateMessageEntity> = listOf()
    ) {
        val localMateMessageDataSource = Mockito.mock(LocalMateMessageDataSource::class.java)

        Mockito.`when`(localMateMessageDataSource.getMateMessages(Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt()))
            .thenReturn(mateMessageModels)
        Mockito.`when`(localMateMessageDataSource.insertMateMessage(AnyUtility.any(MateMessageEntity::class.java)))
            .thenAnswer { }

        val networkResponseString = generateNetworkResponseWithCount(networkResponseMessageCount)
        val networkMateMessageDataSource = NetworkTestContext.generateTestRetrofit(
            NetworkTestContext.generateDefaultTestInterceptor(code, networkResponseString)
        ).create(NetworkMateMessageDataSource::class.java)

        val webSocketUpdateMateMessageDataSource = Mockito.mock(
            WebSocketUpdateMateMessageDataSource::class.java)

        Mockito.`when`(webSocketUpdateMateMessageDataSource.updateFlow)
            .thenReturn(MutableSharedFlow())

        mMateMessageDataRepository = MateMessageDataRepository(
            localMateMessageDataSource, networkMateMessageDataSource,
            webSocketUpdateMateMessageDataSource
        )

        mResultListAtomicRef = AtomicReference<List<Result>>(listOf())
        GlobalScope.launch(Dispatchers.IO) {
            mMateMessageDataRepository.resultFlow.collect {
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
    fun getLocalMateMessagesTest() {
        val chatId = 0L
        val mateMessageEntities = listOf(
            MateMessageEntity(0, chatId, 0, "test", 100),
            MateMessageEntity(1, chatId, 1, "test 2", 200),
        )

        initDataRepository(mateMessageModels = mateMessageEntities)

        val job = GlobalScope.launch {
            mMateMessageDataRepository.getMessages(String(), chatId, mateMessageEntities.size)
        }

        while (mResultListAtomicRef.get().isEmpty()) { }

        val result = mResultListAtomicRef.get().first()

        Assert.assertEquals(result::class, GetMessagesResult::class)

        val resultCast = result as GetMessagesResult

        for (message in mateMessageEntities) {
            Assert.assertNotNull(resultCast.messages.find { it.id == message.id })
        }

        job.cancel()
    }

    @Test
    fun getMateChatFromNetworkTest() {
        val count = 2
        val chatId = 0L

        initDataRepository(networkResponseMessageCount = count)

        val job = GlobalScope.launch {
            mMateMessageDataRepository.getMessages(String(), chatId, count)
        }

        while (mResultListAtomicRef.get().isEmpty()) { }

        val result = mResultListAtomicRef.get().last()

        Assert.assertEquals(result::class, GetMessagesResult::class)

        val resultCast = result as GetMessagesResult

        Assert.assertEquals(count, resultCast.messages.size)

        job.cancel()
    }
}