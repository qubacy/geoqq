package com.qubacy.geoqq.data.mate.message.repository.source.local

import android.content.ContentValues
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.qubacy.geoqq._common._test._common.util.assertion.AssertUtils
import com.qubacy.geoqq.data._common.repository.source_common.local.database.LocalDatabaseDataSourceTest
import com.qubacy.geoqq.data._common.repository.source_common.local.database._common._test.insertable.LocalInsertableDatabaseDataSourceTest
import com.qubacy.geoqq.data.mate.chat.repository.source.local.entity.MateChatEntity
import com.qubacy.geoqq.data.mate.message.repository.source.local.entity.MateMessageEntity
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LocalMateMessageDataSourceTest :
    LocalDatabaseDataSourceTest(),
    LocalInsertableDatabaseDataSourceTest<MateChatEntity>
{
    companion object {
        val DEFAULT_MATE_CHAT = MateChatEntity(0, 0, 0, null)

        val DEFAULT_TIME = 1000L
    }

    private lateinit var mLocalMateMessageDataSource: LocalMateMessageDataSource

    override fun setup() {
        super.setup()

        mLocalMateMessageDataSource = mDatabase.mateMessageDao()

        initDefaultMateChat()
    }

    private fun initDefaultMateChat() {
        insertItems(mDatabase, MateChatEntity.TABLE_NAME, listOf(DEFAULT_MATE_CHAT))
    }

    @Test
    fun insertMateMessageThenGetItTest() {
        val expectedMessageEntity = generateMessages(count = 1).first()

        mLocalMateMessageDataSource.insertMessage(expectedMessageEntity)

        val gottenMessageEntity =
            mLocalMateMessageDataSource.getMessage(
                expectedMessageEntity.chatId, expectedMessageEntity.id)

        Assert.assertEquals(expectedMessageEntity, gottenMessageEntity)
    }

    @Test
    fun getMateMessagesTest() {
        val messageChunkSize = 5
        val secondMessageChunkOffset = 0
        val firstMessageChunkOffset = secondMessageChunkOffset + messageChunkSize

        val expectedSecondMessageChunkOffset = firstMessageChunkOffset
        val expectedFirstMessageChunkOffset = secondMessageChunkOffset
        val expectedSecondMessageChunk = generateMessages(
            secondMessageChunkOffset, messageChunkSize).reversed()
        val expectedFirstMessageChunk = generateMessages(
            firstMessageChunkOffset, messageChunkSize).reversed()

        for (message in expectedSecondMessageChunk + expectedFirstMessageChunk)
            mLocalMateMessageDataSource.insertMessage(message)

        val gottenFirstMessageChunk = mLocalMateMessageDataSource.getMessages(
            DEFAULT_MATE_CHAT.id, expectedFirstMessageChunkOffset, messageChunkSize)
        val gottenSecondMessageChunk = mLocalMateMessageDataSource.getMessages(
            DEFAULT_MATE_CHAT.id, expectedSecondMessageChunkOffset, messageChunkSize)

        AssertUtils.assertEqualContent(expectedFirstMessageChunk, gottenFirstMessageChunk)
        AssertUtils.assertEqualContent(expectedSecondMessageChunk, gottenSecondMessageChunk)
    }

    @Test
    fun updateMateMessageTest() {
        val initMessageEntity = generateMessages(count = 1).first()
        val expectedUpdatedMessageEntity = initMessageEntity.copy(text = "updated text")

        mLocalMateMessageDataSource.insertMessage(initMessageEntity)
        mLocalMateMessageDataSource.updateMessage(expectedUpdatedMessageEntity)

        val gottenUpdatedMessageEntity = mLocalMateMessageDataSource
            .getMessage(expectedUpdatedMessageEntity.chatId, expectedUpdatedMessageEntity.id)

        Assert.assertEquals(expectedUpdatedMessageEntity, gottenUpdatedMessageEntity)
    }

    @Test
    fun deleteMateMessageTest() {
        val messageToDelete = generateMessages(count = 1).first()

        mLocalMateMessageDataSource.insertMessage(messageToDelete)
        mLocalMateMessageDataSource.deleteMessage(messageToDelete)

        val gottenUpdatedMessageEntity = mLocalMateMessageDataSource
            .getMessage(messageToDelete.chatId, messageToDelete.id)

        Assert.assertNull(gottenUpdatedMessageEntity)
    }

    @Test
    fun saveMessagesTest() {
        val initMessages = generateMessages(count = 3)
        val messagesToSave = generateMessages(offset = 1, count = 3).toMutableList().apply {
            this[0] = this[0].copy(text = "updated text")
        }
        val expectedMessages = messagesToSave.toMutableList().apply {
            add(0, initMessages.first())
        }

        for (message in initMessages)
            mLocalMateMessageDataSource.insertMessage(message)

        mLocalMateMessageDataSource.saveMessages(messagesToSave)

        val gottenMessages = mLocalMateMessageDataSource
            .getMessages(DEFAULT_MATE_CHAT.id, 0, expectedMessages.size)

        AssertUtils.assertEqualContent(expectedMessages, gottenMessages)
    }

    override fun packEntityContent(itemEntity: MateChatEntity): ContentValues {
        return ContentValues().apply {
            put(MateChatEntity.ID_PROP_NAME, DEFAULT_MATE_CHAT.id)
            put(MateChatEntity.USER_ID_PROP_NAME, DEFAULT_MATE_CHAT.userId)
            put(MateChatEntity.NEW_MESSAGE_COUNT_PROP_NAME, DEFAULT_MATE_CHAT.newMessageCount)
            put(MateChatEntity.LAST_MESSAGE_ID_PROP_NAME, DEFAULT_MATE_CHAT.lastMessageId)
        }
    }

    private fun generateMessages(
        offset: Int = 0,
        count: Int
    ): List<MateMessageEntity> {
        return IntRange(offset, offset + count - 1).map {
            val id = it.toLong()

            MateMessageEntity(id, DEFAULT_MATE_CHAT.id, DEFAULT_MATE_CHAT.userId,
                "test $id", DEFAULT_TIME)
        }
    }
}