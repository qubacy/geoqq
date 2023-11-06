package com.qubacy.geoqq.data.mate.chat.repository.source.local

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.qubacy.geoqq.data.common.repository.source.local.DatabaseSourceTest
import com.qubacy.geoqq.data.mate.chat.repository.source.local.model.MateChatEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.atomic.AtomicReference

@RunWith(AndroidJUnit4::class)
class LocalMateChatDataSourceTest : DatabaseSourceTest() {
    private lateinit var mLocalMateChatDataSource: LocalMateChatDataSource

    private var mChatsReference = AtomicReference(listOf<MateChatEntity>())
    private var mCurChatCollectionJob: Job? = null

    private fun initChatFlow(count: Int) {
        val chatFlow = mLocalMateChatDataSource.getChats(count)

        mCurChatCollectionJob = GlobalScope.launch(Dispatchers.IO) {
            chatFlow.collect {
                mChatsReference.set(it)
            }
        }
    }

    @Before
    fun setup() {
        mLocalMateChatDataSource = mDatabase.getMateChatDAO()
    }

    @Test
    fun insertingTwoChatsThenGettingThemTest() {
        val chatEntities = listOf(
            MateChatEntity(0, 0, 1, 0),
            MateChatEntity(1, 0, 1, 0)
        )

        initChatFlow(chatEntities.size)

        runBlocking {
            for (chatEntity in chatEntities) {
                mLocalMateChatDataSource.insertChat(chatEntity)
            }

            try {
                while (mChatsReference.get().size != chatEntities.size) { }

                val gottenChats = mChatsReference.get()

                Assert.assertNotNull(gottenChats)
                Assert.assertNotNull(gottenChats.find { it.id == chatEntities[0].id })
                Assert.assertNotNull(gottenChats.find { it.id == chatEntities[1].id })

            } finally {
                for (chatEntity in chatEntities) {
                    mCurChatCollectionJob?.cancel()
                    mLocalMateChatDataSource.deleteChat(chatEntity)
                }
            }
        }
    }

    @Test
    fun updateChatTest() {
        val originalChat = MateChatEntity(0, 0, 0, 0)
        val updatedChat = MateChatEntity(0, 0, 1, 1)

        initChatFlow(1)

        runBlocking {
            mLocalMateChatDataSource.insertChat(originalChat)

            try {
                mLocalMateChatDataSource.updateChat(updatedChat)

                while (mChatsReference.get().isEmpty()) { }

                val gottenChats = mChatsReference.get()

                Assert.assertFalse(gottenChats.isEmpty())

                val gottenChat = gottenChats.first()

                Assert.assertEquals(updatedChat, gottenChat)

            } finally {
                mCurChatCollectionJob?.cancel()

                if (mChatsReference.get().first() == updatedChat)
                    mLocalMateChatDataSource.deleteChat(updatedChat)
                else
                    mLocalMateChatDataSource.deleteChat(originalChat)
            }
        }
    }
}