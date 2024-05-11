package com.qubacy.geoqq.data.mate.chat.repository._common.source.local.database.impl

import com.qubacy.geoqq.data.mate.chat.repository._common.source.local.database._common.LocalMateChatDatabaseDataSource
import com.qubacy.geoqq.data.mate.chat.repository._common.source.local.database._common.dao.LocalMateChatDatabaseDataSourceDao
import com.qubacy.geoqq.data.mate.chat.repository._common.source.local.database._common.entity.MateChatEntity
import com.qubacy.geoqq.data.mate.message.repository._common.source.local.database._common.LocalMateMessageDatabaseDataSource
import com.qubacy.geoqq.data.mate.message.repository._common.source.local.database._common.entity.MateMessageEntity
import javax.inject.Inject

class LocalMateChatDatabaseDataSourceImpl @Inject constructor(
    private val mLocalMateChatDatabaseDataSourceDao: LocalMateChatDatabaseDataSourceDao,
    private val mLocalMateMessageDatabaseDataSource: LocalMateMessageDatabaseDataSource
) : LocalMateChatDatabaseDataSource {
    override fun getChats(offset: Int, count: Int): Map<MateChatEntity, MateMessageEntity?> {
        return mLocalMateChatDatabaseDataSourceDao.getChats(offset, count)
    }

    override fun getChatById(chatId: Long): Map<MateChatEntity, MateMessageEntity?> {
        return mLocalMateChatDatabaseDataSourceDao.getChatById(chatId)
    }

    override fun updateChat(chat: MateChatEntity) {
        return mLocalMateChatDatabaseDataSourceDao.updateChat(chat)
    }

    override fun updateChatWithLastMessage(
        chatLastMessageEntityPair: Pair<MateChatEntity, MateMessageEntity?>
    ) {
        updateChat(chatLastMessageEntityPair.first)
        chatLastMessageEntityPair.second?.also {
            mLocalMateMessageDatabaseDataSource.saveMessage(it)
        }
    }

    override fun insertChat(chat: MateChatEntity) {
        return mLocalMateChatDatabaseDataSourceDao.insertChat(chat)
    }

    override fun insertChatWithLastMessage(
        chatLastMessageEntityPair: Pair<MateChatEntity, MateMessageEntity?>
    ) {
        insertChat(chatLastMessageEntityPair.first)
        chatLastMessageEntityPair.second?.also {
            mLocalMateMessageDatabaseDataSource.insertMessage(it)
        }
    }

    override fun deleteChat(chat: MateChatEntity) {
        return mLocalMateChatDatabaseDataSourceDao.deleteChat(chat)
    }

    override fun deleteChatsByIds(chatIds: List<Long>) {
        return mLocalMateChatDatabaseDataSourceDao.deleteChatsByIds(chatIds)
    }

    override fun deleteOtherChatsByIds(chatIds: List<Long>) {
        return mLocalMateChatDatabaseDataSourceDao.deleteOtherChatsByIds(chatIds)
    }

    override fun deleteAllChats() {
        return mLocalMateChatDatabaseDataSourceDao.deleteAllChats()
    }
}