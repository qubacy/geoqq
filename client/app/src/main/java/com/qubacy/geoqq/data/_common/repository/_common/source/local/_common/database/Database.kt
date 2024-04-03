package com.qubacy.geoqq.data._common.repository._common.source.local._common.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.qubacy.geoqq.data.error.repository.source.local.LocalErrorDataSource
import com.qubacy.geoqq.data.error.repository.source.local.model.ErrorEntity
import com.qubacy.geoqq.data.mate.chat.repository.source.local.LocalMateChatDataSource
import com.qubacy.geoqq.data.mate.chat.repository.source.local.entity.MateChatEntity
import com.qubacy.geoqq.data.mate.message.repository.source.local.LocalMateMessageDataSource
import com.qubacy.geoqq.data.mate.message.repository.source.local.entity.MateMessageEntity
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

    abstract fun errorDao(): LocalErrorDataSource
    abstract fun mateMessageDao(): LocalMateMessageDataSource
    abstract fun mateChatDao(): LocalMateChatDataSource
    abstract fun userDao(): LocalUserDataSource
}