package com.qubacy.geoqq.data.mate.message.repository._common.source.local.database.impl

import com.qubacy.geoqq.data.mate.message.repository._common.source.local.database._common.LocalMateMessageDatabaseDataSource
import com.qubacy.geoqq.data.mate.message.repository._common.source.local.database._common.dao.LocalMateMessageDatabaseDataSourceDao
import com.qubacy.geoqq.data.mate.message.repository._common.source.local.database._common.entity.MateMessageEntity

class LocalMateMessageDatabaseDataSourceImpl(
    private val mLocalMateMessageDatabaseDataSourceDao: LocalMateMessageDatabaseDataSourceDao
) : LocalMateMessageDatabaseDataSource {
    override fun getMessage(chatId: Long, messageId: Long): MateMessageEntity? {
        return mLocalMateMessageDatabaseDataSourceDao.getMessage(chatId, messageId)
    }

    override fun getMessages(chatId: Long, offset: Int, count: Int): List<MateMessageEntity> {
        return mLocalMateMessageDatabaseDataSourceDao.getMessages(chatId, offset, count)
    }

    override fun insertMessage(mateMessage: MateMessageEntity) {
        return mLocalMateMessageDatabaseDataSourceDao.insertMessage(mateMessage)
    }

    override fun updateMessage(mateMessage: MateMessageEntity) {
        return mLocalMateMessageDatabaseDataSourceDao.updateMessage(mateMessage)
    }

    override fun deleteMessage(mateMessage: MateMessageEntity) {
        return mLocalMateMessageDatabaseDataSourceDao.deleteMessage(mateMessage)
    }

    override fun deleteMessagesByIds(chatId: Long, messageIds: List<Long>) {
        return mLocalMateMessageDatabaseDataSourceDao.deleteMessagesByIds(chatId, messageIds)
    }

    override fun deleteOtherMessagesByIds(chatId: Long, messageIds: List<Long>) {
        return mLocalMateMessageDatabaseDataSourceDao.deleteOtherMessagesByIds(chatId, messageIds)
    }

    override fun deleteAllMessages(chatId: Long) {
        return mLocalMateMessageDatabaseDataSourceDao.deleteAllMessages(chatId)
    }
}