package com.qubacy.geoqq.data.mate.chat.repository

import com.qubacy.geoqq.common.util.mock.AnyUtility
import com.qubacy.geoqq.data.common.repository.common.result.common.Result
import com.qubacy.geoqq.data.common.repository.network.NetworkTestContext
import com.qubacy.geoqq.data.mate.chat.repository.result.GetChatsResult
import com.qubacy.geoqq.data.mate.chat.repository.source.local.LocalMateChatDataSource
import com.qubacy.geoqq.data.mate.chat.repository.source.local.model.MateChatEntity
import com.qubacy.geoqq.data.mate.chat.repository.source.local.model.MateChatWithLastMessageModel
import com.qubacy.geoqq.data.mate.chat.repository.source.network.NetworkMateChatDataSource
import com.qubacy.geoqq.data.mate.chat.repository.source.websocket.WebSocketUpdateMateChatDataSource
import com.qubacy.geoqq.data.mate.message.repository.source.local.LocalMateMessageDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import java.util.concurrent.atomic.AtomicReference

class MateChatDataRepositoryTest() {
    private lateinit var mMateChatDataRepository: MateChatDataRepository
    private lateinit var mResultListAtomicRef: AtomicReference<List<Result>>

    private fun generateNetworkResponseWithCount(count: Int): String {
        val responseStringBuilder = StringBuilder("{\"chats\":[")

        for (i in 0 until count)  {
            responseStringBuilder
                .append("{\"id\":$i, \"user-id\":$i, \"new-message-count\":0}")
            responseStringBuilder.append(if (i != count - 1) "," else "")
        }

        return responseStringBuilder.append("]}").toString()
    }

    private fun initDataRepository(
        code: Int = 200,
        networkResponseChatCount: Int = 0,
        mateChatWithLastMessageModels: List<MateChatWithLastMessageModel> = listOf()
    ) {
        val localMateChatDataSource = Mockito.mock(LocalMateChatDataSource::class.java)

        Mockito.`when`(localMateChatDataSource.getChats(Mockito.anyInt(), Mockito.anyInt()))
            .thenReturn(mateChatWithLastMessageModels)
        Mockito.`when`(localMateChatDataSource.insertChat(AnyUtility.any(MateChatEntity::class.java)))
            .thenAnswer { }

        val networkResponseString = generateNetworkResponseWithCount(networkResponseChatCount)
        val networkMateChatDataSource = NetworkTestContext.generateTestRetrofit(
            NetworkTestContext.generateDefaultTestInterceptor(code, networkResponseString)
        ).create(NetworkMateChatDataSource::class.java)

        val localMateMessageDataSource = Mockito.mock(LocalMateMessageDataSource::class.java)



        val webSocketUpdateMateChatDataSource = Mockito.mock(
            WebSocketUpdateMateChatDataSource::class.java)

        Mockito.`when`(webSocketUpdateMateChatDataSource.updateFlow)
            .thenReturn(MutableSharedFlow())

        mMateChatDataRepository = MateChatDataRepository(
            localMateChatDataSource, networkMateChatDataSource,
            localMateMessageDataSource, webSocketUpdateMateChatDataSource
        )

        mResultListAtomicRef = AtomicReference<List<Result>>(listOf())
        GlobalScope.launch(Dispatchers.IO) {
            mMateChatDataRepository.resultFlow.collect {
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
    fun getLocalMateChatDataFlowTest() {
        val mateChatWithLastMessageModelList = listOf(
            MateChatWithLastMessageModel(
                MateChatEntity(0, 0, 0, null), null),
            MateChatWithLastMessageModel(
                MateChatEntity(1, 1, 0, null), null),
        )

        initDataRepository(mateChatWithLastMessageModels = mateChatWithLastMessageModelList)

        val job = GlobalScope.launch {
            mMateChatDataRepository
                .getChats(String(), mateChatWithLastMessageModelList.size)
        }

        while (mResultListAtomicRef.get().isEmpty()) { }

        val result = mResultListAtomicRef.get().first()

        Assert.assertEquals(result::class, GetChatsResult::class)

        val resultCast = result as GetChatsResult

        for (chat in mateChatWithLastMessageModelList) {
            Assert.assertNotNull(resultCast.chats.find { it.id == chat.mateChatEntity.id })
        }

        job.cancel()
    }

    @Test
    fun getMateChatFromNetworkTest() {
        val count = 2

        initDataRepository(networkResponseChatCount = count)

        val job = GlobalScope.launch {
            mMateChatDataRepository.getChats(String(), count)
        }

        while (mResultListAtomicRef.get().isEmpty()) { }

        val result = mResultListAtomicRef.get().last()

        Assert.assertEquals(result::class, GetChatsResult::class)

        val resultCast = result as GetChatsResult

        Assert.assertEquals(count, resultCast.chats.size)

        job.cancel()
    }
}