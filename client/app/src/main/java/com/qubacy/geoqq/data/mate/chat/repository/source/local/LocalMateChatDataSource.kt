package com.qubacy.geoqq.data.mate.chat.repository.source.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.qubacy.geoqq.data.common.repository.common.source.DataSource
import com.qubacy.geoqq.data.mate.chat.repository.source.local.model.MateChatEntity
import com.qubacy.geoqq.data.mate.chat.repository.source.local.model.MateChatWithLastMessageModel
import com.qubacy.geoqq.data.mate.message.repository.source.local.model.MateMessageEntity

@Dao
interface LocalMateChatDataSource : DataSource {
    @Query(
        "SELECT ${MateChatEntity.TABLE_NAME}.${MateChatEntity.CHAT_ID_PROP_NAME} as ${MateChatEntity.CHAT_ID_PROP_NAME}, " +
                "${MateChatEntity.TABLE_NAME}.${MateChatEntity.USER_ID_PROP_NAME} as ${MateChatEntity.USER_ID_PROP_NAME}, " +
                "${MateChatEntity.NEW_MESSAGE_COUNT_PROP_NAME}, " +
                "${MateChatEntity.LAST_MESSAGE_ID_PROP_NAME} " +
        "FROM ${MateChatEntity.TABLE_NAME} " +
        "LEFT JOIN ${MateMessageEntity.TABLE_NAME} ON ${MateMessageEntity.TABLE_NAME}.${MateMessageEntity.ID_PROP_NAME} = " +
                "${MateChatEntity.LAST_MESSAGE_ID_PROP_NAME} " +
        "AND ${MateChatEntity.TABLE_NAME}.${MateChatEntity.CHAT_ID_PROP_NAME} = " +
                "${MateMessageEntity.TABLE_NAME}.${MateMessageEntity.CHAT_ID_PROP_NAME} " +
        "ORDER BY ${MateMessageEntity.TIME_PROP_NAME} DESC " +
        "LIMIT :offset, :count"
    )
    fun getChats(offset: Int, count: Int): List<MateChatWithLastMessageModel>

    @Query("SELECT * FROM ${MateChatEntity.TABLE_NAME}" +
            " WHERE ${MateChatEntity.CHAT_ID_PROP_NAME} = :chatId"
    )
    fun getChatById(chatId: Long): MateChatWithLastMessageModel?

    @Update
    fun updateChat(chat: MateChatEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertChat(chat: MateChatEntity)

    @Delete()
    fun deleteChat(chat: MateChatEntity)
}