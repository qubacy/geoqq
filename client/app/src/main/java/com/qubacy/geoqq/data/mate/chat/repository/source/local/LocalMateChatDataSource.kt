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
        "WHERE ${MateChatEntity.ID_PROP_NAME} NOT IN (:chatIds)"
    )
    fun deleteOtherChatsByIds(chatIds: List<Long>)

    @Query(
        "DELETE FROM ${MateChatEntity.TABLE_NAME}"
    )
    fun deleteAllChats()

//    fun deleteChatsOlderChatWithId(database: Database, chatId: Long, isInclusive: Boolean = false) {
//        val query = SimpleSQLiteQuery(
//            "BEGIN;" +
//            "CREATE TEMP TABLE _SortedRowIndexChatId(row_index PRIMARY KEY, chat_id INTEGER);" +
//            "CREATE TEMP TABLE _GivenChatIdRowIndex(chat_id PRIMARY KEY, row_index INTEGER);" +
//            "INSERT INTO _SortedRowIndexChatId" +
//            "SELECT ROW_NUMBER() OVER(ORDER BY ${MateMessageEntity.TABLE_NAME}.${MateMessageEntity.TIME_PROP_NAME} DESC, ${MateChatEntity.TABLE_NAME}.${MateChatEntity.ID_PROP_NAME} DESC) row_index, ${MateChatEntity.TABLE_NAME}.${MateChatEntity.ID_PROP_NAME} as chat_id" +
//            "FROM ${MateChatEntity.TABLE_NAME}" +
//            "LEFT JOIN ${MateMessageEntity.TABLE_NAME}" +
//            "ON ${MateMessageEntity.TABLE_NAME}.${MateMessageEntity.ID_PROP_NAME} = ${MateChatEntity.TABLE_NAME}.${MateChatEntity.LAST_MESSAGE_ID_PROP_NAME}" +
//            "AND ${MateMessageEntity.TABLE_NAME}.${MateMessageEntity.CHAT_ID_PROP_NAME} = ${MateChatEntity.TABLE_NAME}.${MateChatEntity.ID_PROP_NAME};" +
//            "INSERT INTO _GivenChatIdRowIndex" +
//            "SELECT chat_id, row_index FROM _SortedRowIndexChatId WHERE chat_id = $chatId LIMIT 1;" +
//            "DELETE FROM ${MateChatEntity.TABLE_NAME} WHERE ${MateChatEntity.TABLE_NAME}.${MateChatEntity.ID_PROP_NAME} IN (SELECT _SortedRowIndexChatId.chat_id" +
//            "FROM _SortedRowIndexChatId, _GivenChatIdRowIndex" +
//            "WHERE _SortedRowIndexChatId.row_index ${if (isInclusive) ">=" else ">"} _GivenChatIdRowIndex.row_index;" +
//            "DROP TABLE _SortedRowIndexChatId;" +
//            "DROP TABLE _GivenChatIdRowIndex;" +
//            "END;"
//        )
//
//        database.query(query)
//    }

    fun saveChats(chatLastMessageEntityPairList: List<Pair<MateChatEntity, MateMessageEntity?>>) {
        for (chatLastMessageEntityPair in chatLastMessageEntityPairList) {
            val getLocalChatResult = getChatById(chatLastMessageEntityPair.first.id)

            if (getLocalChatResult.isEmpty())
                insertChatWithLastMessage(chatLastMessageEntityPair)
            else updateChatWithLastMessage(chatLastMessageEntityPair)
        }
    }
}