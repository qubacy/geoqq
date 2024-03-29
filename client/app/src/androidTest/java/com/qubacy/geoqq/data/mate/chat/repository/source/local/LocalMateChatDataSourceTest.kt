package com.qubacy.geoqq.data.mate.chat.repository.source.local

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.qubacy.geoqq.data._common.repository.source_common.local.database.LocalDatabaseDataSourceTest
import com.qubacy.geoqq.data.mate.chat.repository.source.local.entity.MateChatEntity
import com.qubacy.geoqq.data.mate.message.repository.source.local.entity.MateMessageEntity
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LocalMateChatDataSourceTest : LocalDatabaseDataSourceTest() {
    companion object {
        const val DEFAULT_USER_ID = 0L
        const val DEFAULT_NEW_MESSAGE_COUNT = 0
        const val DEFAULT_LAST_MESSAGE_ID = 0L
        const val DEFAULT_LAST_MESSAGE_TIME = 100L
    }

    private lateinit var mLocalMateChatDataSource: LocalMateChatDataSource

    override fun setup() {
        super.setup()

        initLocalMateChatDataSource()
    }

    private fun initLocalMateChatDataSource() {
        mLocalMateChatDataSource = mDatabase.mateChatDao()
    }

    @Test
    fun insertChatThenGetItTest() {
        val expectedChat = generateChats(1).first()

        mLocalMateChatDataSource.insertChat(expectedChat)

        val gottenChat = mLocalMateChatDataSource.getChatById(expectedChat.id).keys.first()

        Assert.assertEquals(expectedChat, gottenChat)
    }

    @Test
    fun insertChatWithLastMessageThenGetItTest() {
        val expectedChatLastMessageEntityPair = Pair(
            MateChatEntity(0, 0, 0, 0),
            MateMessageEntity(0, 0, 0, "", 100)
        )

        mLocalMateChatDataSource.insertChatWithLastMessage(expectedChatLastMessageEntityPair)

        val gottenChatWithLastMessage = mLocalMateChatDataSource
            .getChatById(expectedChatLastMessageEntityPair.first.id).entries.first().toPair()

        Assert.assertEquals(expectedChatLastMessageEntityPair, gottenChatWithLastMessage)
    }

    @Test
    fun getChatsTest() {
        val chatChunkSize = 5
        val secondChatChunkOffset = 0
        val firstChatChunkOffset = secondChatChunkOffset + chatChunkSize

        val expectedSecondChatChunkOffset = firstChatChunkOffset
        val expectedFirstChatChunkOffset = secondChatChunkOffset
        val expectedSecondChatChunk = generateChatLastMessageMap(
            chatChunkSize, secondChatChunkOffset, true)
        val expectedFirstChatChunk = generateChatLastMessageMap(
            chatChunkSize, firstChatChunkOffset, true)

        for (chatWithLastMessage in expectedSecondChatChunk + expectedFirstChatChunk)
            mLocalMateChatDataSource.insertChatWithLastMessage(chatWithLastMessage.toPair())

        val gottenFirstChatChunk = mLocalMateChatDataSource.getChats(
            expectedFirstChatChunkOffset, chatChunkSize)
        val gottenSecondChatChunk = mLocalMateChatDataSource.getChats(
            expectedSecondChatChunkOffset, chatChunkSize)

        assertChatWithLastMessageChunk(expectedFirstChatChunk, gottenFirstChatChunk)
        assertChatWithLastMessageChunk(expectedSecondChatChunk, gottenSecondChatChunk)
    }

    @Test
    fun updateChatTest() {
        val initChatEntry = generateChats(1).first()
        val expectedChatEntity = initChatEntry.copy(lastMessageId = 1)

        mLocalMateChatDataSource.insertChat(initChatEntry)
        mLocalMateChatDataSource.updateChat(expectedChatEntity)

        val gottenChat = mLocalMateChatDataSource
            .getChatById(expectedChatEntity.id).entries.first().toPair().first

        Assert.assertEquals(expectedChatEntity, gottenChat)
    }

    @Test
    fun updateChatWithLastMessageTest() {
        val initChatLastMessageEntityPair = Pair(
            MateChatEntity(0, 0, 0, 0),
            MateMessageEntity(0, 0, 0, "", 100)
        )
        val expectedChatLastMessageEntityPair = Pair(
            initChatLastMessageEntityPair.first.copy(lastMessageId = 1),
            initChatLastMessageEntityPair.second.copy(id = 1, text = "updated text")
        )

        mLocalMateChatDataSource.insertChatWithLastMessage(initChatLastMessageEntityPair)
        mLocalMateChatDataSource.updateChatWithLastMessage(expectedChatLastMessageEntityPair)

        val gottenChatWithLastMessage = mLocalMateChatDataSource
            .getChatById(expectedChatLastMessageEntityPair.first.id).entries.first().toPair()

        Assert.assertEquals(expectedChatLastMessageEntityPair, gottenChatWithLastMessage)
    }

    @Test
    fun deleteChatTest() {
        val chatToDelete = generateChats(1).first()

        mLocalMateChatDataSource.insertChat(chatToDelete)
        mLocalMateChatDataSource.deleteChat(chatToDelete)

        val gottenChats = mLocalMateChatDataSource.getChatById(chatToDelete.id)

        Assert.assertTrue(gottenChats.isEmpty())
    }

    @Test
    fun saveChatsTest() {
        val initChats = generateChatLastMessageMap(3)
        val chatToUpdate = initChats.entries.last().key
        val chatsToSave = generateChatLastMessageMap(3, 1).toMutableMap().apply {
            this[chatToUpdate] = this[chatToUpdate]!!.copy(text = "updated text")
        }
        val expectedChats = chatsToSave.toMutableMap().apply {
            val firstChatEntry = initChats.entries.first()

            this[firstChatEntry.key] = firstChatEntry.value
        }

        for (chatWithLastMessage in initChats)
            mLocalMateChatDataSource.insertChatWithLastMessage(chatWithLastMessage.toPair())

        mLocalMateChatDataSource.saveChats(chatsToSave.toList())

        val gottenChats = mLocalMateChatDataSource.getChats(0, expectedChats.size)

        assertChatWithLastMessageChunk(expectedChats, gottenChats)
    }

    private fun assertChatWithLastMessageChunk(
        expectedChatWithLastMessageChunk: Map<MateChatEntity, MateMessageEntity?>,
        gottenChatWithLastMessageChunk: Map<MateChatEntity, MateMessageEntity?>
    ) {
        Assert.assertEquals(
            expectedChatWithLastMessageChunk.size,
            gottenChatWithLastMessageChunk.size
        )

        for (expectedChatWithLastMessage in expectedChatWithLastMessageChunk) {
            Assert.assertTrue(gottenChatWithLastMessageChunk.contains(expectedChatWithLastMessage.key))
            Assert.assertEquals(
                expectedChatWithLastMessage.value,
                gottenChatWithLastMessageChunk[expectedChatWithLastMessage.key]
            )
        }
    }

    private fun generateChatLastMessageMap(
        count: Int,
        offset: Int = 0,
        isReversed: Boolean = false
    ): Map<MateChatEntity, MateMessageEntity> {
        val map = mutableMapOf<MateChatEntity, MateMessageEntity>()

        IntRange(offset, offset + count - 1)
            .let { if (isReversed) it.reversed() else it }
            .forEach {
                val chatEntity = generateChats(1, it).first()
                val lastMessageEntity = MateMessageEntity(
                    DEFAULT_LAST_MESSAGE_ID, chatEntity.id, DEFAULT_USER_ID,
                    "test ${chatEntity.id}", DEFAULT_LAST_MESSAGE_TIME + it
                )

                map[chatEntity] = lastMessageEntity
            }

        return map
    }

    private fun generateChats(
        count: Int,
        offset: Int = 0
    ): List<MateChatEntity> {
        return IntRange(offset, offset + count - 1).map {
            val id = it.toLong()

            MateChatEntity(
                id,
                DEFAULT_USER_ID,
                DEFAULT_NEW_MESSAGE_COUNT,
                DEFAULT_LAST_MESSAGE_ID
            )
        }
    }
}