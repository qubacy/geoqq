package com.qubacy.geoqq.data.mate.chat.repository

import com.qubacy.geoqq.common.AnyUtility
import com.qubacy.geoqq.data.common.repository.network.NetworkTestContext
import com.qubacy.geoqq.data.mate.chat.model.DataMateChat
import com.qubacy.geoqq.data.mate.chat.repository.result.GetChatsResult
import com.qubacy.geoqq.data.mate.chat.repository.source.local.LocalMateChatDataSource
import com.qubacy.geoqq.data.mate.chat.repository.source.local.model.MateChatEntity
import com.qubacy.geoqq.data.mate.chat.repository.source.network.NetworkMateChatDataSource
import com.qubacy.geoqq.data.mate.chat.repository.source.websocket.WebSocketUpdateMateChatDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import java.util.concurrent.atomic.AtomicReference

class MateChatDataRepositoryTest() {
    private lateinit var mMateChatDataRepository: MateChatDataRepository
    private lateinit var mCurMateChatEntityFlow: MutableSharedFlow<List<MateChatEntity>>
    private lateinit var mCurMateChatEntityListReference: AtomicReference<List<MateChatEntity>>
    private lateinit var mCurCoroutineJob: Job

    private lateinit var mLocalMateChatDataSourceInsertionBuffer: MutableList<MateChatEntity>

    private fun generateNetworkResponseWithCount(count: Int): String {
        val responseStringBuilder = StringBuilder("{\"chats\":[")

        for (i in 0 until count)  {
            responseStringBuilder
                .append("{\"id\":$i, \"user-id\":$i, \"new-message-count\":0, \"last-message-id\":0}")
            responseStringBuilder.append(if (i != count - 1) "," else "")
        }

        return responseStringBuilder.append("]}").toString()
    }

    private fun initDataRepository(
        code: Int = 200,
        networkResponseChatCount: Int = 0
    ) {
        mCurMateChatEntityFlow = MutableSharedFlow()
        mLocalMateChatDataSourceInsertionBuffer = mutableListOf()

        val localMateChatDataSource = Mockito.mock(LocalMateChatDataSource::class.java)

        Mockito.`when`(localMateChatDataSource.getChats(Mockito.anyInt()))
            .thenReturn(mCurMateChatEntityFlow)
        Mockito.`when`(localMateChatDataSource.insertChat(AnyUtility.any(MateChatEntity::class.java)))
            .thenAnswer {
                runBlocking {
                    val entity = it.arguments[0] as MateChatEntity

                    mLocalMateChatDataSourceInsertionBuffer.add(entity)

                    if (mLocalMateChatDataSourceInsertionBuffer.size < networkResponseChatCount)
                        return@runBlocking

                    mCurMateChatEntityFlow.emit(mLocalMateChatDataSourceInsertionBuffer)
                }
            }

        val networkResponseString = generateNetworkResponseWithCount(networkResponseChatCount)
        val networkMateChatDataSource = NetworkTestContext.generateTestRetrofit(
            NetworkTestContext.generateDefaultTestInterceptor(code, networkResponseString)
        ).create(NetworkMateChatDataSource::class.java)

        val webSocketUpdateMateChatDataSource = Mockito.mock(
            WebSocketUpdateMateChatDataSource::class.java)

        mMateChatDataRepository = MateChatDataRepository(
            localMateChatDataSource, networkMateChatDataSource, webSocketUpdateMateChatDataSource)

        mCurMateChatEntityListReference = AtomicReference<List<MateChatEntity>>()
        mCurCoroutineJob = GlobalScope.launch(Dispatchers.IO) {
            mCurMateChatEntityFlow.collect {
                mCurMateChatEntityListReference.set(it)
            }
        }
    }

    @Before
    fun setup() {

    }

    @Test
    fun getLocalMateChatDataFlowTest() {
        val mateChatEntityList = listOf(
            MateChatEntity(0, 0, 0, 0),
            MateChatEntity(1, 1, 0, 0),
        )

        initDataRepository()

        runBlocking {
            try {
                val getChatsResult = mMateChatDataRepository.getChats(String(), mateChatEntityList.size)

                Assert.assertEquals(getChatsResult::class, GetChatsResult::class)

                mCurMateChatEntityFlow.emit(mateChatEntityList)

                while (mCurMateChatEntityListReference.get() == null) { }
                while (mCurMateChatEntityListReference.get().isEmpty()) { }

                Assert.assertEquals(mateChatEntityList, mCurMateChatEntityListReference.get())

            } finally {
                mCurCoroutineJob.cancel()
            }
        }
    }

    @Test
    fun getMateChatFromNetworkTest() {
        val count = 2

        initDataRepository(networkResponseChatCount = count)

        runBlocking {
            try {
                val getChatsResult = mMateChatDataRepository.getChats(String(), count)

                Assert.assertEquals(getChatsResult::class, GetChatsResult::class)

                while (mCurMateChatEntityListReference.get() == null) { }
                while (mCurMateChatEntityListReference.get().size < count) { }

                val mateChatEntityList = mCurMateChatEntityListReference.get()

                Assert.assertEquals(count, mateChatEntityList.size)

            } finally {
                mCurCoroutineJob.cancel()
            }
        }
    }
}