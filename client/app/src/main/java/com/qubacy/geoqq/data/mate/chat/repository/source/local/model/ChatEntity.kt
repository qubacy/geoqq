package com.qubacy.geoqq.data.mate.chat.repository.source.local.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.qubacy.geoqq.data.mate.chat.model.DataMateChat

@Entity(tableName = ChatEntity.TABLE_NAME)
data class ChatEntity(
    @PrimaryKey()
    @ColumnInfo(name = CHAT_ID_PROP_NAME) val id: Long,
    @ColumnInfo(name = USER_ID_PROP_NAME) val userId: Long,
    @ColumnInfo(name = NEW_MESSAGE_COUNT_PROP_NAME) val newMessageCount: Int,
    @ColumnInfo(name = LAST_MESSAGE_ID_PROP_NAME) val lastMessageId: Long
) {
    companion object {
        const val TABLE_NAME = "Chat"

        const val CHAT_ID_PROP_NAME = "id"
        const val USER_ID_PROP_NAME = "user_id"
        const val NEW_MESSAGE_COUNT_PROP_NAME = "new_message_count"
        const val LAST_MESSAGE_ID_PROP_NAME = "last_message_id"
    }
}

fun ChatEntity.toDataMateChat(): DataMateChat {
    return DataMateChat(id, userId, newMessageCount, lastMessageId)
}