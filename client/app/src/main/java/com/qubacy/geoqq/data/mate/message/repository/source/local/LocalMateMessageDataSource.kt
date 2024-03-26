package com.qubacy.geoqq.data.mate.message.repository.source.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.qubacy.geoqq.data._common.repository._common.source._common.DataSource
import com.qubacy.geoqq.data.mate.message.repository.source.local.entity.MateMessageEntity

@Dao
interface LocalMateMessageDataSource : DataSource {
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

    fun saveMessage(message: MateMessageEntity) {
        val localMessage = getMessage(message.chatId, message.id)

        if(message == localMessage) return

        if (localMessage == null) insertMessage(message)
        else updateMessage(message)
    }

    fun saveMessages(messages: List<MateMessageEntity>) {
        for (message in messages) saveMessage(message)
    }
}