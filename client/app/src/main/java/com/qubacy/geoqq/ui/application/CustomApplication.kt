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
import com.qubacy.geoqq.data._common.repository._common.source.local.datastore.token.LocalTokenDataStoreDataSource
import com.qubacy.geoqq.data._common.repository._common.source.local.datastore.token.tokenDataStore
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.client.interceptor.auth.module.AuthorizationHttpInterceptorModule
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.client.module.HttpClientModule
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.executor.HttpCallExecutor
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.response.error.json.adapter.ErrorJsonAdapter
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.token.HttpTokenDataSource
import com.yandex.mapkit.MapKitFactory
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class CustomApplication : Application() {
    companion object {
        const val TAG = "CustomApplication"
    }

    private lateinit var mHttpApi: HttpApi
    val httpApi get() = mHttpApi

    private lateinit var mDB: Database
    val db get() = mDB

    private lateinit var mLocalTokenDataStoreDataSource: LocalTokenDataStoreDataSource
    val localTokenDataStoreDataSource get() = mLocalTokenDataStoreDataSource

    private val mSettingsDataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
    val settingsDataStore get() = mSettingsDataStore

    override fun onCreate() {
        super.onCreate()

        mDB = buildDatabase()

        initSharedLocalDataSources(mDB)

        mHttpApi = buildHttpApi(mDB, mLocalTokenDataStoreDataSource)

        MapKitFactory.setApiKey(BuildConfig.MAPKIT_API_KEY)
    }

    private fun initSharedLocalDataSources(database: Database) {
        val tokenDataStore = this.tokenDataStore

        mLocalTokenDataStoreDataSource = LocalTokenDataStoreDataSource(tokenDataStore)
    }

    // todo: is this ok?:
    private fun buildHttpApi(
        database: Database,
        localTokenDataSource: LocalTokenDataStoreDataSource
    ): HttpApi {
        val errorDataSource = LocalErrorDataSourceModule
            .provideLocalErrorDataSourceModule(database.errorDao())

        val httpCallExecutor = HttpCallExecutor(errorDataSource, ErrorJsonAdapter())
        val httpTokenDataSource = HttpTokenDataSource(httpCallExecutor)
        val errorJsonAdapter = ErrorJsonAdapter()

        val authorizationHttpInterceptor = AuthorizationHttpInterceptorModule
            .provideAuthorizationHttpInterceptor(
                errorDataSource, errorJsonAdapter, localTokenDataSource, httpTokenDataSource)
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