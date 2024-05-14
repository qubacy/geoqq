package com.qubacy.geoqq.data.mate.message.repository.source.local.database._common.dao

import android.content.ContentValues
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.qubacy.geoqq._common._test._common.util.assertion.AssertUtils
import com.qubacy.geoqq.data._common.repository._common.source.local.database._common.LocalDatabaseDataSourceTest
import com.qubacy.geoqq.data._common.repository._common.source.local.database._common._test.insertable.LocalInsertableDatabaseDataSourceTest
import com.qubacy.geoqq.data.mate.chat.repository._common.source.local.database._common.entity.MateChatEntity
import com.qubacy.geoqq.data.mate.chat.repository.source.local.database._common._test.context.LocalMateChatDatabaseDataSourceTestContext
import com.qubacy.geoqq.data.mate.message.repository._common.source.local.database._common.dao.LocalMateMessageDatabaseDataSourceDao
import com.qubacy.geoqq.data.mate.message.repository._common.source.local.database._common.entity.MateMessageEntity
import com.qubacy.geoqq.data.mate.message.repository.source.local.database._common._test.context.LocalMateMessageDatabaseDataSourceTestContext
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LocalMateMessageDatabaseDataSourceDaoTest :
    LocalDatabaseDataSourceTest(),
    LocalInsertableDatabaseDataSourceTest<MateChatEntity>
{
    companion object {
        val DEFAULT_MATE_CHAT_ENTITY = LocalMateChatDatabaseDataSourceTestContext
            .DEFAULT_MATE_CHAT_ENTITY
        val DEFAULT_MATE_MESSAGE_ENTITY = LocalMateMessageDatabaseDataSourceTestContext
            .DEFAULT_MATE_MESSAGE_ENTITY
    }

    private lateinit var mLocalMateMessageDataSource: LocalMateMessageDatabaseDataSourceDao

    override fun setup() {
        super.setup()

        mLocalMateMessageDataSource = mDatabase.mateMessageDao()

        initDefaultMateChat()
    }

    private fun initDefaultMateChat() {
        insertItems(mDatabase, MateChatEntity.TABLE_NAME, listOf(DEFAULT_MATE_CHAT_ENTITY))
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
            DEFAULT_MATE_CHAT_ENTITY.id, expectedFirstMessageChunkOffset, messageChunkSize)
        val gottenSecondMessageChunk = mLocalMateMessageDataSource.getMessages(
            DEFAULT_MATE_CHAT_ENTITY.id, expectedSecondMessageChunkOffset, messageChunkSize)

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

        val gottenDeletedMessageEntity = mLocalMateMessageDataSource
            .getMessage(messageToDelete.chatId, messageToDelete.id)

        Assert.assertNull(gottenDeletedMessageEntity)
    }

    @Test
    fun deleteMateMessagesByIdsTest() {
        val chatId = 0L
        val messagesToDelete = generateMessages(count = 2)

        messagesToDelete.forEach { mLocalMateMessageDataSource.insertMessage(it) }

        mLocalMateMessageDataSource.deleteMessagesByIds(chatId, messagesToDelete.map { it.id })

        val gottenDeletedMessageEntities = mLocalMateMessageDataSource
            .getMessages(messagesToDelete.first().chatId, 0, messagesToDelete.size)

        Assert.assertTrue(gottenDeletedMessageEntities.isEmpty())
    }

//    @Test
//    fun saveMessagesTest() {
//        val initMessages = generateMessages(count = 3)
//        val messagesToSave = generateMessages(offset = 1, count = 3).toMutableList().apply {
//            this[0] = this[0].copy(text = "updated text")
//        }
//        val expectedMessages = messagesToSave.toMutableList().apply {
//            add(0, initMessages.first())
//        }
//
//        for (message in initMessages)
//            mLocalMateMessageDataSource.insertMessage(message)
//
//        mLocalMateMessageDataSource.saveMessages(messagesToSave)
//
//        val gottenMessages = mLocalMateMessageDataSource
//            .getMessages(DEFAULT_MATE_CHAT_ENTITY.id, 0, expectedMessages.size)
//
//        AssertUtils.assertEqualContent(expectedMessages, gottenMessages)
//    }

    override fun packEntityContent(itemEntity: MateChatEntity): ContentValues {
        return ContentValues().apply {
            put(MateChatEntity.ID_PROP_NAME, DEFAULT_MATE_CHAT_ENTITY.id)
            put(MateChatEntity.USER_ID_PROP_NAME, DEFAULT_MATE_CHAT_ENTITY.userId)
            put(MateChatEntity.NEW_MESSAGE_COUNT_PROP_NAME, DEFAULT_MATE_CHAT_ENTITY.newMessageCount)
            put(MateChatEntity.LAST_MESSAGE_ID_PROP_NAME, DEFAULT_MATE_CHAT_ENTITY.lastMessageId)
        }
    }

    private fun generateMessages(
        offset: Int = 0,
        count: Int
    ): List<MateMessageEntity> {
        return IntRange(offset, offset + count - 1).map {
            val id = it.toLong()

            DEFAULT_MATE_MESSAGE_ENTITY.copy(id = id, text = "test $id")
        }
    }
}