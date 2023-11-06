package com.qubacy.geoqq.data.mate.chat.repository.source.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.qubacy.geoqq.data.common.repository.common.source.DataSource
import com.qubacy.geoqq.data.mate.chat.repository.source.local.model.ChatEntity

@Dao
interface LocalMateChatDataSource : DataSource {
    @Query("SELECT * FROM ${ChatEntity.TABLE_NAME}")
    fun getChats(): List<ChatEntity>

    @Query("SELECT * FROM ${ChatEntity.TABLE_NAME} WHERE ${ChatEntity.CHAT_ID_PROP_NAME} = :chatId")
    fun getChatById(chatId: Long): ChatEntity?

    @Update
    fun updateChat(chat: ChatEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertChat(chat: ChatEntity)

    @Delete()
    fun deleteChat(chat: ChatEntity)
}