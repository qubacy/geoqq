package com.qubacy.geoqq.ui.application

import android.app.Application
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.qubacy.geoqq.BuildConfig
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.api.HttpApi
import com.qubacy.geoqq.data._common.repository._common.source.local.database._common.Database
import com.qubacy.geoqq.data._common.repository._common.source.local.database.error.module.LocalErrorDataSourceModule
import com.qubacy.geoqq.data._common.repository._common.source.local.datastore.token.module.LocalTokenDataStoreDataSourceModule
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.client.interceptor.auth.module.AuthorizationHttpInterceptorModule
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.client.module.HttpClientModule
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.executor.HttpCallExecutor
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.response.error.json.adapter.ErrorJsonAdapter
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.token.HttpTokenDataSource
import com.yandex.mapkit.MapKitFactory
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class CustomApplication : Application() {
    private lateinit var mHttpApi: HttpApi
    val httpApi get() = mHttpApi

    private lateinit var mDB: Database
    val db get() = mDB

    private val mSettingsDataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
    val settingsDataStore get() = mSettingsDataStore

    override fun onCreate() {
        super.onCreate()

        mDB = buildDatabase()
        mHttpApi = buildHttpApi(mDB)

        MapKitFactory.setApiKey(BuildConfig.MAPKIT_API_KEY)
    }

    // todo: is this ok?:
    private fun buildHttpApi(database: Database): HttpApi {
        val errorDataSource = LocalErrorDataSourceModule
            .provideLocalErrorDataSourceModule(database.errorDao())
        val localTokenDataSource = LocalTokenDataStoreDataSourceModule
            .provideLocalTokenDataStoreDataSource(this)

        val httpCallExecutor = HttpCallExecutor(errorDataSource, ErrorJsonAdapter())
        val httpTokenDataSource = HttpTokenDataSource(httpCallExecutor)

        val authorizationHttpInterceptor = AuthorizationHttpInterceptorModule
            .provideAuthorizationHttpInterceptor(
                errorDataSource, localTokenDataSource, httpTokenDataSource)
        val okHttpClient = HttpClientModule
            .provideHttpClient(errorDataSource, authorizationHttpInterceptor)

        return HttpApi(okHttpClient).also {
            httpTokenDataSource.setHttpTokenDataSourceApi(it.tokenApi) // todo: dirty..;
        }
    }

    private fun buildDatabase(): Database {
        return Room.databaseBuilder(
            this, Database::class.java, Database.DATABASE_NAME)
            .fallbackToDestructiveMigration()
            .createFromAsset(Database.DATABASE_NAME)
            .build()
    }
}