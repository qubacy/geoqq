package com.qubacy.geoqq.data.mate.chat.repository.source.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = MateChatEntity.TABLE_NAME,
    foreignKeys = [])
data class MateChatEntity(
    @PrimaryKey()
    @ColumnInfo(name = ID_PROP_NAME) val id: Long,
    @ColumnInfo(name = USER_ID_PROP_NAME) val userId: Long,
    @ColumnInfo(
        name = NEW_MESSAGE_COUNT_PROP_NAME,
        defaultValue = NEW_MESSAGE_COUNT_DEFAULT_VALUE
    ) val newMessageCount: Int,
    @ColumnInfo(name = LAST_MESSAGE_ID_PROP_NAME) val lastMessageId: Long?
) {
    companion object {
        const val TABLE_NAME = "MateChat"

        const val ID_PROP_NAME = "id"
        const val USER_ID_PROP_NAME = "user_id"
        const val NEW_MESSAGE_COUNT_PROP_NAME = "new_message_count"
        const val LAST_MESSAGE_ID_PROP_NAME = "last_message_id"

        const val NEW_MESSAGE_COUNT_DEFAULT_VALUE = "0"
    }
}