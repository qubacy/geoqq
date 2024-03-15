package com.qubacy.geoqq.ui.application

import android.app.Application
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.qubacy.geoqq.data._common.repository._common.source.local.database.Database
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class CustomApplication : Application() {
    private lateinit var mDB: Database
    val db get() = mDB

    private val mSettingsDataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
    val settingsDataStore get() = mSettingsDataStore

    override fun onCreate() {
        super.onCreate()

        mDB = buildDatabase()
    }

    private fun buildDatabase(): Database {
        return Room.databaseBuilder(
            this, Database::class.java, Database.DATABASE_NAME)
//            .fallbackToDestructiveMigration()
            .createFromAsset(Database.DATABASE_NAME)
            .build()
    }
}