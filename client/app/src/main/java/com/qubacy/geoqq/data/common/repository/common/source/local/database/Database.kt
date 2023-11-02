package com.qubacy.geoqq.data.common.repository.common.source.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.qubacy.geoqq.data.user.repository.source.local.LocalUserDataSource
import com.qubacy.geoqq.data.user.repository.source.local.entity.UserEntity

@Database(entities = [UserEntity::class], version = 1)
abstract class Database : RoomDatabase() {
    companion object {
        const val DATABASE_NAME = "geoqq_db"
    }

    abstract fun getUserDAO(): LocalUserDataSource
}