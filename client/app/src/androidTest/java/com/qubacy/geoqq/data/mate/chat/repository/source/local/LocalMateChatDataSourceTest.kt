package com.qubacy.geoqq.data.mate.chat.repository.source.local

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.qubacy.geoqq.data.common.repository.source.local.DatabaseSourceTest
import com.qubacy.geoqq.data.mate.chat.repository.source.local.model.MateChatEntity
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LocalMateChatDataSourceTest : DatabaseSourceTest() {
    private lateinit var mLocalMateChatDataSource: LocalMateChatDataSource

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

        runBlocking {
            for (chatEntity in chatEntities) {
                mLocalMateChatDataSource.insertChat(chatEntity)
            }

            try {
                val gottenChats = mLocalMateChatDataSource.getChats(chatEntities.size)

                Assert.assertNotNull(gottenChats)
                Assert.assertNotNull(gottenChats.find { it.id == chatEntities[0].id })
                Assert.assertNotNull(gottenChats.find { it.id == chatEntities[1].id })

            } finally {
                for (chatEntity in chatEntities) {
                    mLocalMateChatDataSource.deleteChat(chatEntity)
                }
            }
        }
    }

    @Test
    fun updateChatTest() {
        val originalChat = MateChatEntity(0, 0, 0, 0)
        val updatedChat = MateChatEntity(0, 0, 1, 1)

        runBlocking {
            mLocalMateChatDataSource.insertChat(originalChat)

            var gottenChat: MateChatEntity? = null

            try {
                mLocalMateChatDataSource.updateChat(updatedChat)

                val gottenChats = mLocalMateChatDataSource.getChats(1)

                Assert.assertFalse(gottenChats.isEmpty())

                gottenChat = gottenChats.first()

                Assert.assertEquals(updatedChat, gottenChat)

            } finally {
                if (gottenChat == updatedChat)
                    mLocalMateChatDataSource.deleteChat(updatedChat)
                else
                    mLocalMateChatDataSource.deleteChat(originalChat)
            }
        }
    }
}