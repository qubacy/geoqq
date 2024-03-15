package com.qubacy.geoqq.data._common.repository._common.source.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.qubacy.geoqq.data.error.repository.source.local.LocalErrorDataSource
import com.qubacy.geoqq.data.error.repository.source.local.model.ErrorEntity

@Database(
    entities = [ErrorEntity::class],
    version = 1
)
abstract class Database : RoomDatabase() {
    companion object {
        const val DATABASE_NAME = "application.db"
    }

    abstract fun errorDao(): LocalErrorDataSource
}