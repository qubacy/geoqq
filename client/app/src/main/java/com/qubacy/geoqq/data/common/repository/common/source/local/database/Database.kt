package com.qubacy.geoqq.data.common.repository.common.source.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.qubacy.geoqq.data.error.repository.source.local.LocalErrorDataSource
import com.qubacy.geoqq.data.error.repository.source.local.model.ErrorEntity
import com.qubacy.geoqq.data.user.repository.source.local.LocalUserDataSource
import com.qubacy.geoqq.data.user.repository.source.local.entity.UserEntity

@Database(entities = [UserEntity::class, ErrorEntity::class], version = 5)
abstract class Database : RoomDatabase() {
    companion object {
        const val DATABASE_NAME = "geoqq.db"
    }

    abstract fun getUserDAO(): LocalUserDataSource
    abstract fun getErrorDAO(): LocalErrorDataSource
}