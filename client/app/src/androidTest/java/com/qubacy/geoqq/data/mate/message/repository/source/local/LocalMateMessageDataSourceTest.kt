package com.qubacy.geoqq.data.mate.message.repository.source.local

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.qubacy.geoqq.data.common.repository.source.local.DatabaseSourceTest
import com.qubacy.geoqq.data.mate.chat.repository.source.local.LocalMateChatDataSource
import com.qubacy.geoqq.data.mate.chat.repository.source.local.model.MateChatEntity
import com.qubacy.geoqq.data.mate.message.repository.source.local.model.MateMessageEntity
import com.qubacy.geoqq.data.user.repository.source.local.LocalUserDataSource
import com.qubacy.geoqq.data.user.repository.source.local.entity.UserEntity
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LocalMateMessageDataSourceTest(

) : DatabaseSourceTest() {
    private lateinit var mLocalMateMessageDataSource: LocalMateMessageDataSource
    private lateinit var mLocalUserDataSource: LocalUserDataSource
    private lateinit var mLocalMateChatDataSource: LocalMateChatDataSource

    @Before
    fun setup() {
        mLocalMateMessageDataSource = mDatabase.getMateMessageDAO()
        mLocalUserDataSource = mDatabase.getUserDAO()
        mLocalMateChatDataSource = mDatabase.getMateChatDAO()
    }

    @Test
    fun insertTwoMessagesThenGetThemTest() {
        val user = UserEntity(0, "test", "test", 0, 0)
        val chat = MateChatEntity(0, user.id, 0, null)
        val messages = listOf(
            MateMessageEntity(0, chat.id, user.id, "test 1", 123123123),
            MateMessageEntity(1, chat.id, user.id, "test 2", 123123123),
        )

        mLocalUserDataSource.insertUser(user)

        try {
            mLocalMateChatDataSource.insertChat(chat)

            try {
                for (message in messages)
                    mLocalMateMessageDataSource.insertMateMessage(message)

                try {
                    val gottenMessages = mLocalMateMessageDataSource.getMateMessages(chat.id, 0, messages.size)

                    Assert.assertTrue(gottenMessages.isNotEmpty())

                    for (originalMessage in messages) {
                        Assert.assertNotNull(gottenMessages.find { it.id == originalMessage.id })
                    }

                } finally {
                    for (message in messages)
                        mLocalMateMessageDataSource.deleteMateMessage(message)
                }

            } finally {
                mLocalMateChatDataSource.deleteChat(chat)
            }

        } finally {
            mLocalUserDataSource.deleteUser(user)
        }
    }

    @Test
    fun insertMessageThenUpdateItTest() {
        val user = UserEntity(0, "test", "test", 0, 0)
        val chat = MateChatEntity(0, user.id, 0, null)
        val message =
            MateMessageEntity(1, chat.id, user.id, "test 2", 123123123)
        val updatedMessage =
            MateMessageEntity(1, chat.id, user.id, "test 2 updated", 123123123)

        mLocalUserDataSource.insertUser(user)

        try {
            mLocalMateChatDataSource.insertChat(chat)

            try {
                mLocalMateMessageDataSource.insertMateMessage(message)

                var isUpdated = false

                try {
                    mLocalMateMessageDataSource.updateMateMessage(updatedMessage)
                    isUpdated = true

                    val gottenMessage =
                        mLocalMateMessageDataSource.getMateMessage(chat.id, updatedMessage.id)

                    Assert.assertNotNull(gottenMessage)
                    Assert.assertEquals(updatedMessage.text, gottenMessage!!.text)

                } finally {
                    if (isUpdated)
                        mLocalMateMessageDataSource.deleteMateMessage(updatedMessage)
                    else
                        mLocalMateMessageDataSource.deleteMateMessage(message)
                }

            } finally {
                mLocalMateChatDataSource.deleteChat(chat)
            }

        } finally {
            mLocalUserDataSource.deleteUser(user)
        }
    }
}