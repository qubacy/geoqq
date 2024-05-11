package com.qubacy.geoqq.data.mate.chat.repository._common.source.local.database._common.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.qubacy.geoqq.data.mate.chat.repository._common.source.local.database._common.entity.MateChatEntity
import com.qubacy.geoqq.data.mate.message.repository._common.source.local.database._common.entity.MateMessageEntity

@Dao
interface LocalMateChatDatabaseDataSourceDao {
    @Query(
        "SELECT *" +
        "FROM ${MateChatEntity.TABLE_NAME} " +
        "LEFT JOIN ${MateMessageEntity.TABLE_NAME} " +
        "ON ${MateMessageEntity.TABLE_NAME}.${MateMessageEntity.ID_PROP_NAME} = " +
        "${MateChatEntity.LAST_MESSAGE_ID_PROP_NAME} " +
        "AND ${MateMessageEntity.TABLE_NAME}.${MateMessageEntity.CHAT_ID_PROP_NAME} = " +
        "${MateChatEntity.TABLE_NAME}.${MateChatEntity.ID_PROP_NAME} " +
        "ORDER BY ${MateMessageEntity.TIME_PROP_NAME} DESC, ${MateChatEntity.ID_PROP_NAME} DESC " +
        "LIMIT :offset, :count"
    )
    fun getChats(offset: Int, count: Int): Map<MateChatEntity, MateMessageEntity?>

    @Query(
        "SELECT *" +
        "FROM ${MateChatEntity.TABLE_NAME} " +
        "LEFT JOIN ${MateMessageEntity.TABLE_NAME} " +
        "ON ${MateMessageEntity.TABLE_NAME}.${MateMessageEntity.ID_PROP_NAME} = " +
        "${MateChatEntity.LAST_MESSAGE_ID_PROP_NAME} " +
        "AND ${MateMessageEntity.TABLE_NAME}.${MateMessageEntity.CHAT_ID_PROP_NAME} = " +
        "${MateChatEntity.TABLE_NAME}.${MateChatEntity.ID_PROP_NAME} " +
        "WHERE ${MateChatEntity.TABLE_NAME}.${MateChatEntity.ID_PROP_NAME} = :chatId"
    )
    fun getChatById(chatId: Long): Map<MateChatEntity, MateMessageEntity?>

    @Update
    fun updateChat(chat: MateChatEntity)

    @Insert()
    fun insertChat(chat: MateChatEntity)

    @Delete()
    fun deleteChat(chat: MateChatEntity)

    @Query(
        "DELETE FROM ${MateChatEntity.TABLE_NAME} " +
        "WHERE ${MateChatEntity.ID_PROP_NAME} IN (:chatIds)"
    )
    fun deleteChatsByIds(chatIds: List<Long>)

    @Query(
        "DELETE FROM ${MateChatEntity.TABLE_NAME} " +
        "WHERE ${MateChatEntity.ID_PROP_NAME} NOT IN (:chatIds)"
    )
    fun deleteOtherChatsByIds(chatIds: List<Long>)

    @Query(
        "DELETE FROM ${MateChatEntity.TABLE_NAME}"
    )
    fun deleteAllChats()
}