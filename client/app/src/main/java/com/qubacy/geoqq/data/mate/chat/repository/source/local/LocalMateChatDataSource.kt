package com.qubacy.geoqq.data.mate.chat.repository.source.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.qubacy.geoqq.data.mate.chat.repository.source.local.entity.MateChatEntity
import com.qubacy.geoqq.data.mate.message.repository.source.local.LocalMateMessageDataSource
import com.qubacy.geoqq.data.mate.message.repository.source.local.entity.MateMessageEntity

@Dao
interface LocalMateChatDataSource : LocalMateMessageDataSource {
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

    @Transaction
    fun updateChatWithLastMessage(
        chatLastMessageEntityPair: Pair<MateChatEntity, MateMessageEntity?>
    ) {
        updateChat(chatLastMessageEntityPair.first)
        chatLastMessageEntityPair.second?.also { saveMessage(it) }
    }

    @Insert()
    fun insertChat(chat: MateChatEntity)

    @Transaction
    fun insertChatWithLastMessage(
        chatLastMessageEntityPair: Pair<MateChatEntity, MateMessageEntity?>
    ) {
        insertChat(chatLastMessageEntityPair.first)
        chatLastMessageEntityPair.second?.also { insertMessage(it) }
    }

    @Delete()
    fun deleteChat(chat: MateChatEntity)

    @Query(
        "DELETE FROM ${MateChatEntity.TABLE_NAME} " +
        "WHERE ${MateChatEntity.ID_PROP_NAME} IN (:chatIds)"
    )
    fun deleteChatsByIds(chatIds: List<Long>)

    @Query(
        "DELETE FROM ${MateChatEntity.TABLE_NAME} " +
        "WHERE ${MateChatEntity.ID_PROP_NAME} IN " +
        "(SELECT ${MateChatEntity.ID_PROP_NAME}" +
        "FROM ${MateChatEntity.}" +
        ")"
    )
    fun deleteChatsOlderChatWithId(chatId: Long)

    fun saveChats(chatLastMessageEntityPairList: List<Pair<MateChatEntity, MateMessageEntity?>>) {
        for (chatLastMessageEntityPair in chatLastMessageEntityPairList) {
            val getLocalChatResult = getChatById(chatLastMessageEntityPair.first.id)

            if (getLocalChatResult.isEmpty())
                insertChatWithLastMessage(chatLastMessageEntityPair)
            else updateChatWithLastMessage(chatLastMessageEntityPair)
        }
    }
}