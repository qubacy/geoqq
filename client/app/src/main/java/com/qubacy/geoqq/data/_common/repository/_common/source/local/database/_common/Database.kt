package com.qubacy.geoqq.data._common.repository._common.source.local.database._common

import androidx.room.Database
import androidx.room.RoomDatabase
import com.qubacy.geoqq.data._common.repository._common.source.local.database.error.dao.LocalErrorDataSourceDao
import com.qubacy.geoqq.data._common.repository._common.source.local.database.error.dao.model.ErrorEntity
import com.qubacy.geoqq.data.mate.chat.repository.source.local.LocalMateChatDataSource
import com.qubacy.geoqq.data.mate.chat.repository.source.local.entity.MateChatEntity
import com.qubacy.geoqq.data.mate.message.repository.source.local.LocalMateMessageDataSource
import com.qubacy.geoqq.data.mate.message.repository.source.local.entity.MateMessageEntity
import com.qubacy.geoqq.data.auth.repository.source.local.database.LocalAuthDatabaseDataSource
import com.qubacy.geoqq.data.user.repository.source.local.LocalUserDataSource
import com.qubacy.geoqq.data.user.repository.source.local.entity.UserEntity

@Database(
    entities = [
        ErrorEntity::class,
        MateMessageEntity::class,
        MateChatEntity::class,
        UserEntity::class
   ],
    version = 1
)
abstract class Database : RoomDatabase() {
    companion object {
        const val DATABASE_NAME = "application.db"
    }

    abstract fun errorDao(): LocalErrorDataSourceDao
    abstract fun mateMessageDao(): LocalMateMessageDataSource
    abstract fun mateChatDao(): LocalMateChatDataSource
    abstract fun userDao(): LocalUserDataSource
    abstract fun authDao(): LocalAuthDatabaseDataSource
}