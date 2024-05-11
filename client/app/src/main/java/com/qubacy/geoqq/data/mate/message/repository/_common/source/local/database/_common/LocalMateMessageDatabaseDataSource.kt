package com.qubacy.geoqq.data.mate.message.repository._common.source.local.database._common

import com.qubacy.geoqq.data.mate.message.repository._common.source.local.database._common.entity.MateMessageEntity

interface LocalMateMessageDatabaseDataSource {
    fun getMessage(chatId: Long, messageId: Long): MateMessageEntity?
    fun getMessages(chatId: Long, offset: Int, count: Int): List<MateMessageEntity>
    fun insertMessage(mateMessage: MateMessageEntity)
    fun updateMessage(mateMessage: MateMessageEntity)
    fun deleteMessage(mateMessage: MateMessageEntity)
    fun deleteMessagesByIds(chatId: Long, messageIds: List<Long>)
    fun deleteOtherMessagesByIds(chatId: Long, messageIds: List<Long>)
    fun deleteAllMessages(chatId: Long)
    fun saveMessage(message: MateMessageEntity) {
        val localMessage = getMessage(message.chatId, message.id)

        if (message == localMessage) return

        if (localMessage == null) insertMessage(message)
        else updateMessage(message)
    }
    fun saveMessages(messages: List<MateMessageEntity>) {
        for (message in messages) saveMessage(message)
    }
}