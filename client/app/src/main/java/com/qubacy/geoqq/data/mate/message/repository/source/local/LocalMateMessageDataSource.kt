package com.qubacy.geoqq.data.mate.message.repository.source.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.qubacy.geoqq.data.common.repository.common.source.DataSource
import com.qubacy.geoqq.data.mate.message.repository.source.local.model.MateMessageEntity

@Dao
interface LocalMateMessageDataSource : DataSource {
    @Query("SELECT * FROM ${MateMessageEntity.TABLE_NAME} " +
            "WHERE ${MateMessageEntity.ID_PROP_NAME} = :messageId " +
            "AND ${MateMessageEntity.CHAT_ID_PROP_NAME} = :chatId"
    )
    fun getMateMessage(chatId: Long, messageId: Long): MateMessageEntity?

    @Query("SELECT * FROM ${MateMessageEntity.TABLE_NAME} " +
            "WHERE ${MateMessageEntity.CHAT_ID_PROP_NAME} = :chatId"
    )
    fun getMateMessages(chatId: Long): List<MateMessageEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMateMessage(mateMessage: MateMessageEntity)

    @Update()
    fun updateMateMessage(mateMessage: MateMessageEntity)

    @Delete()
    fun deleteMateMessage(mateMessage: MateMessageEntity)
}