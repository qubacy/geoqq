package com.qubacy.geoqq.data.mate.message.repository._common.source.local.database._common.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import com.qubacy.geoqq.data.mate.chat.repository._common.source.local.database._common.entity.MateChatEntity

@Entity(tableName = MateMessageEntity.TABLE_NAME,
    primaryKeys = [MateMessageEntity.ID_PROP_NAME, MateMessageEntity.CHAT_ID_PROP_NAME],
    foreignKeys = [
        ForeignKey(
            entity = MateChatEntity::class,
            parentColumns = arrayOf(MateChatEntity.ID_PROP_NAME),
            childColumns = arrayOf(MateMessageEntity.CHAT_ID_PROP_NAME),
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class MateMessageEntity(
    @ColumnInfo(name = ID_PROP_NAME) val id: Long,
    @ColumnInfo(name = CHAT_ID_PROP_NAME) val chatId: Long,
    @ColumnInfo(name = USER_ID_PROP_NAME) val userId: Long,
    @ColumnInfo(name = TEXT_PROP_NAME) val text: String,
    @ColumnInfo(name = TIME_PROP_NAME) val timeInSec: Long
) {
    companion object {
        const val TABLE_NAME = "MateMessage"

        const val ID_PROP_NAME = "id"
        const val CHAT_ID_PROP_NAME = "chat_id"
        const val USER_ID_PROP_NAME = "user_id"
        const val TEXT_PROP_NAME = "text"
        const val TIME_PROP_NAME = "time"
    }
}