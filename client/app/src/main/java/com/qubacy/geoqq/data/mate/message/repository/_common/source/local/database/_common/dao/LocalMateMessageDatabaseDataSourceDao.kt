package com.qubacy.geoqq.data.mate.message.repository._common.source.local.database._common.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.qubacy.geoqq.data.mate.message.repository._common.source.local.database._common.entity.MateMessageEntity

@Dao
interface LocalMateMessageDatabaseDataSourceDao {
    @Query("SELECT * FROM ${MateMessageEntity.TABLE_NAME} " +
            "WHERE ${MateMessageEntity.ID_PROP_NAME} = :messageId " +
            "AND ${MateMessageEntity.CHAT_ID_PROP_NAME} = :chatId"
    )
    fun getMessage(chatId: Long, messageId: Long): MateMessageEntity?

    @Query("SELECT * FROM ${MateMessageEntity.TABLE_NAME} " +
            "WHERE ${MateMessageEntity.CHAT_ID_PROP_NAME} = :chatId " +
            "ORDER BY ${MateMessageEntity.ID_PROP_NAME} DESC " +
            "LIMIT :offset, :count"
    )
    fun getMessages(chatId: Long, offset: Int, count: Int): List<MateMessageEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMessage(mateMessage: MateMessageEntity)

    @Update()
    fun updateMessage(mateMessage: MateMessageEntity)

    @Delete()
    fun deleteMessage(mateMessage: MateMessageEntity)

    @Query(
        "DELETE FROM ${MateMessageEntity.TABLE_NAME} " +
        "WHERE ${MateMessageEntity.TABLE_NAME}.${MateMessageEntity.CHAT_ID_PROP_NAME} = :chatId " +
        "AND ${MateMessageEntity.ID_PROP_NAME} IN (:messageIds)"
    )
    fun deleteMessagesByIds(chatId: Long, messageIds: List<Long>)

    @Query(
        "DELETE FROM ${MateMessageEntity.TABLE_NAME} " +
        "WHERE ${MateMessageEntity.TABLE_NAME}.${MateMessageEntity.CHAT_ID_PROP_NAME} = :chatId " +
        "AND ${MateMessageEntity.ID_PROP_NAME} NOT IN (:messageIds)"
    )
    fun deleteOtherMessagesByIds(chatId: Long, messageIds: List<Long>)

    @Query(
        "DELETE FROM ${MateMessageEntity.TABLE_NAME} " +
        "WHERE ${MateMessageEntity.TABLE_NAME}.${MateMessageEntity.CHAT_ID_PROP_NAME} = :chatId"
    )
    fun deleteAllMessages(chatId: Long)
}