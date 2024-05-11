package com.qubacy.geoqq.data.mate.chat.repository._common.source.local.database._common

import com.qubacy.geoqq.data.mate.chat.repository._common.source.local.database._common.entity.MateChatEntity
import com.qubacy.geoqq.data.mate.message.repository._common.source.local.database._common.entity.MateMessageEntity

interface LocalMateChatDatabaseDataSource {
    fun getChats(offset: Int, count: Int): Map<MateChatEntity, MateMessageEntity?>
    fun getChatById(chatId: Long): Map<MateChatEntity, MateMessageEntity?>
    fun updateChat(chat: MateChatEntity)
    fun updateChatWithLastMessage(
        chatLastMessageEntityPair: Pair<MateChatEntity, MateMessageEntity?>
    )
    fun insertChat(chat: MateChatEntity)
    fun insertChatWithLastMessage(
        chatLastMessageEntityPair: Pair<MateChatEntity, MateMessageEntity?>
    )
    fun deleteChat(chat: MateChatEntity)
    fun deleteChatsByIds(chatIds: List<Long>)
    fun deleteOtherChatsByIds(chatIds: List<Long>)
    fun deleteAllChats()
    fun saveChats(chatLastMessageEntityPairList: List<Pair<MateChatEntity, MateMessageEntity?>>) {
        for (chatLastMessageEntityPair in chatLastMessageEntityPairList) {
            val getLocalChatResult = getChatById(chatLastMessageEntityPair.first.id)

            if (getLocalChatResult.isEmpty())
                insertChatWithLastMessage(chatLastMessageEntityPair)
            else updateChatWithLastMessage(chatLastMessageEntityPair)
        }
    }
}