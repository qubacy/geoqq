package com.qubacy.geoqq.data._common.repository._common.source.local.database._common

import androidx.room.Database
import androidx.room.RoomDatabase
import com.qubacy.geoqq.data._common.repository._common.source.local.database.error._common.dao.LocalErrorDataSourceDao
import com.qubacy.geoqq.data._common.repository._common.source.local.database.error._common.dao.model.ErrorEntity
import com.qubacy.geoqq.data.mate.chat.repository._common.source.local.database._common.dao.LocalMateChatDatabaseDataSourceDao
import com.qubacy.geoqq.data.mate.chat.repository._common.source.local.database._common.entity.MateChatEntity
import com.qubacy.geoqq.data.mate.message.repository._common.source.local.database._common.dao.LocalMateMessageDatabaseDataSourceDao
import com.qubacy.geoqq.data.mate.message.repository._common.source.local.database._common.entity.MateMessageEntity
import com.qubacy.geoqq.data.auth.repository._common.source.local.database.impl.LocalAuthDatabaseDataSourceImpl
import com.qubacy.geoqq.data.user.repository._common.source.local.database._common.dao.LocalUserDatabaseDataSourceDao
import com.qubacy.geoqq.data.user.repository._common.source.local.database._common.entity.UserEntity

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
    abstract fun mateMessageDao(): LocalMateMessageDatabaseDataSourceDao
    abstract fun mateChatDao(): LocalMateChatDatabaseDataSourceDao
    abstract fun userDao(): LocalUserDatabaseDataSourceDao
    abstract fun authDao(): LocalAuthDatabaseDataSourceImpl
}