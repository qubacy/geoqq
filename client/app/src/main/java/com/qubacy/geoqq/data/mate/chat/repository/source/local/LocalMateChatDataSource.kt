package com.qubacy.geoqq.data.mate.chat.repository.source.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.qubacy.geoqq.data.common.repository.common.source.DataSource
import com.qubacy.geoqq.data.mate.chat.repository.source.local.model.MateChatEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LocalMateChatDataSource : DataSource {
    @Query("SELECT * FROM ${MateChatEntity.TABLE_NAME} LIMIT :count")
    fun getChats(count: Int): Flow<List<MateChatEntity>>

    @Query("SELECT * FROM ${MateChatEntity.TABLE_NAME} WHERE ${MateChatEntity.CHAT_ID_PROP_NAME} = :chatId")
    fun getChatById(chatId: Long): MateChatEntity?

    @Update
    fun updateChat(chat: MateChatEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertChat(chat: MateChatEntity)

    @Delete()
    fun deleteChat(chat: MateChatEntity)
}