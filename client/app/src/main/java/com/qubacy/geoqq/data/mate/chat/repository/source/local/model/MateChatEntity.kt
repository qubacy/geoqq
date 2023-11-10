package com.qubacy.geoqq.data.mate.chat.repository.source.local.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.qubacy.geoqq.data.mate.message.repository.source.local.model.MateMessageEntity
import com.qubacy.geoqq.data.user.repository.source.local.entity.UserEntity

@Entity(
    tableName = MateChatEntity.TABLE_NAME,
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = arrayOf(UserEntity.ID_PARAM_NAME),
            childColumns = arrayOf(MateChatEntity.USER_ID_PROP_NAME),
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = MateMessageEntity::class,
            parentColumns = arrayOf(MateMessageEntity.CHAT_ID_PROP_NAME, MateMessageEntity.ID_PROP_NAME),
            childColumns = arrayOf(MateChatEntity.CHAT_ID_PROP_NAME, MateChatEntity.LAST_MESSAGE_ID_PROP_NAME),
            onDelete = ForeignKey.SET_NULL
        ),
    ])
data class MateChatEntity(
    @PrimaryKey()
    @ColumnInfo(name = CHAT_ID_PROP_NAME) val id: Long,
    @ColumnInfo(name = USER_ID_PROP_NAME) val userId: Long,
    @ColumnInfo(name = NEW_MESSAGE_COUNT_PROP_NAME) val newMessageCount: Int,
    @ColumnInfo(name = LAST_MESSAGE_ID_PROP_NAME) val lastMessageId: Long?
) {
    companion object {
        const val TABLE_NAME = "MateChat"

        const val CHAT_ID_PROP_NAME = "id"
        const val USER_ID_PROP_NAME = "user_id"
        const val NEW_MESSAGE_COUNT_PROP_NAME = "new_message_count"
        const val LAST_MESSAGE_ID_PROP_NAME = "last_message_id"
    }
}