package com.qubacy.geoqq.data.mate.chat.repository.source.local.database._common.dao

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.qubacy.geoqq._common._test._common.util.assertion.AssertUtils
import com.qubacy.geoqq.data._common.repository._common.source.local.database._common.LocalDatabaseDataSourceTest
import com.qubacy.geoqq.data.mate.chat.repository._common.source.local.database._common.dao.LocalMateChatDatabaseDataSourceDao
import com.qubacy.geoqq.data.mate.chat.repository._common.source.local.database._common.entity.MateChatEntity
import com.qubacy.geoqq.data.mate.chat.repository.source.local.database._common._test.context.LocalMateChatDatabaseDataSourceTestContext
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LocalMateChatDatabaseDataSourceDaoTest : LocalDatabaseDataSourceTest() {
    companion object {
        val DEFAULT_MATE_CHAT_ENTITY = LocalMateChatDatabaseDataSourceTestContext
            .DEFAULT_MATE_CHAT_ENTITY
    }

    private lateinit var mLocalMateChatDataSourceDao: LocalMateChatDatabaseDataSourceDao

    override fun setup() {
        super.setup()

        initLocalMateChatDataSource()
    }

    private fun initLocalMateChatDataSource() {
        mLocalMateChatDataSourceDao = mDatabase.mateChatDao()
    }

    @Test
    fun insertChatThenGetItTest() {
        val expectedChat = generateChats(1).first()

        mLocalMateChatDataSourceDao.insertChat(expectedChat)

        val gottenChat = mLocalMateChatDataSourceDao.getChatById(expectedChat.id).keys.first()

        Assert.assertEquals(expectedChat, gottenChat)
    }

//    @Test
//    fun insertChatWithLastMessageThenGetItTest() {
//        val expectedChatLastMessageEntityPair = Pair(
//            MateChatEntity(0, 0, 0, 0),
//            MateMessageEntity(0, 0, 0, "", 100)
//        )
//
//        mLocalMateChatDataSourceDao.insertChatWithLastMessage(expectedChatLastMessageEntityPair)
//
//        val gottenChatWithLastMessage = mLocalMateChatDataSourceDao
//            .getChatById(expectedChatLastMessageEntityPair.first.id).entries.first().toPair()
//
//        Assert.assertEquals(expectedChatLastMessageEntityPair, gottenChatWithLastMessage)
//    }

    @Test
    fun getChatsTest() {
        val chatChunkSize = 5
        val secondChatChunkOffset = 0
        val firstChatChunkOffset = secondChatChunkOffset + chatChunkSize

        val expectedSecondChatChunkOffset = firstChatChunkOffset
        val expectedFirstChatChunkOffset = secondChatChunkOffset
        val expectedSecondChatChunk = generateChats(chatChunkSize, secondChatChunkOffset)
        val expectedFirstChatChunk = generateChats(chatChunkSize, firstChatChunkOffset)

        for (chat in expectedSecondChatChunk + expectedFirstChatChunk)
            mLocalMateChatDataSourceDao.insertChat(chat)

        val gottenFirstChatChunk = mLocalMateChatDataSourceDao.getChats(
            expectedFirstChatChunkOffset, chatChunkSize).keys.toList()
        val gottenSecondChatChunk = mLocalMateChatDataSourceDao.getChats(
            expectedSecondChatChunkOffset, chatChunkSize).keys.toList()

        AssertUtils.assertEqualContent(expectedFirstChatChunk, gottenFirstChatChunk)
        AssertUtils.assertEqualContent(expectedSecondChatChunk, gottenSecondChatChunk)
    }

    @Test
    fun updateChatTest() {
        val initChatEntry = generateChats(1).first()
        val expectedChatEntity = initChatEntry.copy(lastMessageId = 1)

        mLocalMateChatDataSourceDao.insertChat(initChatEntry)
        mLocalMateChatDataSourceDao.updateChat(expectedChatEntity)

        val gottenChat = mLocalMateChatDataSourceDao
            .getChatById(expectedChatEntity.id).entries.first().toPair().first

        Assert.assertEquals(expectedChatEntity, gottenChat)
    }

//    @Test
//    fun updateChatWithLastMessageTest() {
//        val initChatLastMessageEntityPair = Pair(
//            MateChatEntity(0, 0, 0, 0),
//            MateMessageEntity(0, 0, 0, "", 100)
//        )
//        val expectedChatLastMessageEntityPair = Pair(
//            initChatLastMessageEntityPair.first.copy(lastMessageId = 1),
//            initChatLastMessageEntityPair.second.copy(id = 1, text = "updated text")
//        )
//
//        mLocalMateChatDataSourceDao.insertChatWithLastMessage(initChatLastMessageEntityPair)
//        mLocalMateChatDataSourceDao.updateChatWithLastMessage(expectedChatLastMessageEntityPair)
//
//        val gottenChatWithLastMessage = mLocalMateChatDataSourceDao
//            .getChatById(expectedChatLastMessageEntityPair.first.id).entries.first().toPair()
//
//        Assert.assertEquals(expectedChatLastMessageEntityPair, gottenChatWithLastMessage)
//    }

    @Test
    fun deleteChatTest() {
        val chatToDelete = generateChats(1).first()

        mLocalMateChatDataSourceDao.insertChat(chatToDelete)
        mLocalMateChatDataSourceDao.deleteChat(chatToDelete)

        val gottenChats = mLocalMateChatDataSourceDao.getChatById(chatToDelete.id)

        Assert.assertTrue(gottenChats.isEmpty())
    }

    @Test
    fun deleteChatsByIdsTest() {
        val chatsToDelete = generateChats(2)

        chatsToDelete.forEach { mLocalMateChatDataSourceDao.insertChat(it) }

        mLocalMateChatDataSourceDao.deleteChatsByIds(chatsToDelete.map { it.id })

        val gottenChatEntities = mLocalMateChatDataSourceDao.getChats(0, chatsToDelete.size)

        Assert.assertTrue(gottenChatEntities.isEmpty())
    }

//    @Test
//    fun saveChatsTest() {
//        val initChats = generateChatLastMessageMap(3)
//        val chatToUpdate = initChats.entries.last().key
//        val chatsToSave = generateChatLastMessageMap(3, 1).toMutableMap().apply {
//            this[chatToUpdate] = this[chatToUpdate]!!.copy(text = "updated text")
//        }
//        val expectedChats = chatsToSave.toMutableMap().apply {
//            val firstChatEntry = initChats.entries.first()
//
//            this[firstChatEntry.key] = firstChatEntry.value
//        }
//
//        for (chatWithLastMessage in initChats)
//            mLocalMateChatDataSource.insertChatWithLastMessage(chatWithLastMessage.toPair())
//
//        mLocalMateChatDataSource.saveChats(chatsToSave.toList())
//
//        val gottenChats = mLocalMateChatDataSource.getChats(0, expectedChats.size)
//
//        assertChatWithLastMessageChunk(expectedChats, gottenChats)
//    }

//    private fun assertChatWithLastMessageChunk(
//        expectedChatWithLastMessageChunk: Map<MateChatEntity, MateMessageEntity?>,
//        gottenChatWithLastMessageChunk: Map<MateChatEntity, MateMessageEntity?>
//    ) {
//        Assert.assertEquals(
//            expectedChatWithLastMessageChunk.size,
//            gottenChatWithLastMessageChunk.size
//        )
//
//        for (expectedChatWithLastMessage in expectedChatWithLastMessageChunk) {
//            Assert.assertTrue(gottenChatWithLastMessageChunk.contains(expectedChatWithLastMessage.key))
//            Assert.assertEquals(
//                expectedChatWithLastMessage.value,
//                gottenChatWithLastMessageChunk[expectedChatWithLastMessage.key]
//            )
//        }
//    }

//    private fun generateChatLastMessageMap(
//        count: Int,
//        offset: Int = 0,
//        isReversed: Boolean = false
//    ): Map<MateChatEntity, MateMessageEntity> {
//        val map = mutableMapOf<MateChatEntity, MateMessageEntity>()
//
//        IntRange(offset, offset + count - 1)
//            .let { if (isReversed) it.reversed() else it }
//            .forEach {
//                val chatEntity = generateChats(1, it).first()
//                val lastMessageEntity = MateMessageEntity(
//                    DEFAULT_LAST_MESSAGE_ID, chatEntity.id, DEFAULT_USER_ID,
//                    "test ${chatEntity.id}", DEFAULT_LAST_MESSAGE_TIME + it
//                )
//
//                map[chatEntity] = lastMessageEntity
//            }
//
//        return map
//    }

    private fun generateChats(
        count: Int,
        offset: Int = 0
    ): MutableList<MateChatEntity> {
        return IntRange(offset, offset + count - 1).map {
            val id = it.toLong()

            DEFAULT_MATE_CHAT_ENTITY.copy(id = id)
        }.toMutableList()
    }
}