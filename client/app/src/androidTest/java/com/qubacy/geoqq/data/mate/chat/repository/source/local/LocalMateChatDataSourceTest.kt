package com.qubacy.geoqq.data.mate.chat.repository.source.local

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.qubacy.geoqq.data.common.repository.source.local.DatabaseSourceTest
import com.qubacy.geoqq.data.mate.chat.repository.source.local.model.MateChatEntity
import com.qubacy.geoqq.data.mate.chat.repository.source.local.model.MateChatWithLastMessageModel
import com.qubacy.geoqq.data.mate.message.repository.source.local.LocalMateMessageDataSource
import com.qubacy.geoqq.data.mate.message.repository.source.local.model.MateMessageEntity
import com.qubacy.geoqq.data.user.repository.source.local.LocalUserDataSource
import com.qubacy.geoqq.data.user.repository.source.local.entity.UserEntity
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LocalMateChatDataSourceTest : DatabaseSourceTest() {
    private lateinit var mLocalMateChatDataSource: LocalMateChatDataSource
    private lateinit var mLocalMateMessageDataSource: LocalMateMessageDataSource
    private lateinit var mLocalUserDataSource: LocalUserDataSource

    @Before
    fun setup() {
        mLocalMateChatDataSource = mDatabase.getMateChatDAO()
        mLocalMateMessageDataSource = mDatabase.getMateMessageDAO()
        mLocalUserDataSource = mDatabase.getUserDAO()
    }

    @Test
    fun insertingTwoChatsThenGettingThemTest() {
        val user = UserEntity(0, "test", "test", 0, 0)
        val chatEntities = listOf(
            MateChatEntity(0, user.id, 1, null),
            MateChatEntity(1, user.id, 1, null)
        )

        mLocalUserDataSource.insertUser(user)

        try {
            for (chatEntity in chatEntities) {
                mLocalMateChatDataSource.insertChat(chatEntity)
            }

            try {
                val gottenChats = mLocalMateChatDataSource.getChats(chatEntities.size)

                Assert.assertNotNull(gottenChats)
                Assert.assertNotNull(gottenChats.find { it.mateChatEntity.id == chatEntities[0].id })
                Assert.assertNotNull(gottenChats.find { it.mateChatEntity.id == chatEntities[1].id })

            } finally {
                for (chatEntity in chatEntities) {
                    mLocalMateChatDataSource.deleteChat(chatEntity)
                }
            }
        } finally {
            mLocalUserDataSource.deleteUser(user)
        }
    }

    @Test
    fun updateChatTest() {
        val originalChat = MateChatEntity(0, 0, 0, null)
        val updatedChat = MateChatEntity(0, 0, 1, null)

        mLocalMateChatDataSource.insertChat(originalChat)

        var gottenChat: MateChatEntity? = null

        try {
            mLocalMateChatDataSource.updateChat(updatedChat)

            val gottenChats = mLocalMateChatDataSource.getChats(1)

            Assert.assertFalse(gottenChats.isEmpty())

            gottenChat = gottenChats.first().mateChatEntity

            Assert.assertEquals(updatedChat, gottenChat)

        } finally {
            if (gottenChat == updatedChat)
                mLocalMateChatDataSource.deleteChat(updatedChat)
            else
                mLocalMateChatDataSource.deleteChat(originalChat)
        }
    }

    @Test
    fun getChatWithLastMessageTest() {
        val user = UserEntity(0, "test", "test", 0, 0)
        val chat = MateChatEntity(0, user.id, 0, null)
        val lastMessage = MateMessageEntity(
            0, 0, user.id, "test message", 123123123)
        val updatedChat = MateChatEntity(0, user.id, 0, lastMessage.id)
        val expectedChatModel = MateChatWithLastMessageModel(updatedChat, lastMessage)

        mLocalUserDataSource.insertUser(user)

        try {
            mLocalMateChatDataSource.insertChat(chat)

            try {
                mLocalMateMessageDataSource.insertMateMessage(lastMessage)

                try {
                    mLocalMateChatDataSource.updateChat(updatedChat)

                    val gottenChat = mLocalMateChatDataSource.getChatById(chat.id)

                    Assert.assertNotNull(gottenChat)
                    Assert.assertEquals(
                        expectedChatModel.mateChatEntity.id, gottenChat!!.mateChatEntity.id)

                } finally {
                    //mLocalMateMessageDataSource.deleteMateMessage(lastMessage) // todo: why does it throw an exception?
                }

            } finally {
                mLocalMateChatDataSource.deleteChat(chat)
            }

        } finally {
            mLocalUserDataSource.deleteUser(user)
        }
    }
}