package com.qubacy.geoqq.data._common.repository._common.source.local.database._common._di.module

import android.content.Context
import androidx.room.Room
import com.qubacy.geoqq.data._common.repository._common.source.local.database._common.Database
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
abstract class DatabaseModule {
    companion object {
        @JvmStatic
        @Singleton
        @Provides
        fun provideDatabase(
            context: Context
        ): Database {
            return Room.databaseBuilder(
                context, Database::class.java, Database.DATABASE_NAME
            )
                .fallbackToDestructiveMigration()
                .createFromAsset(Database.DATABASE_NAME)
                .build()
        }
    }
}