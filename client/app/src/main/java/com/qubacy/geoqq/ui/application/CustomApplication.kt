package com.qubacy.geoqq.ui.application

import android.app.Application
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.qubacy.geoqq.BuildConfig
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.rest.api.HttpRestApi
import com.qubacy.geoqq.data._common.repository._common.source.local.database._common.Database
import com.qubacy.geoqq.data._common.repository._common.source.local.database.error.module.LocalErrorDataSourceModule
import com.qubacy.geoqq.data._common.repository._common.source.local.datastore.token.LocalTokenDataStoreDataSource
import com.qubacy.geoqq.data._common.repository._common.source.local.datastore.token.tokenDataStore
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.rest.client.interceptor.auth.module.AuthorizationHttpRestInterceptorModule
import com.qubacy.geoqq.data._common.repository._common.source.remote.http._common.client.module.HttpClientModule
import com.qubacy.geoqq.data._common.repository._common.source.remote.http._common.executor.HttpCallExecutor
import com.qubacy.geoqq.data._common.repository._common.source.remote.http._common.response.error.json.adapter.ErrorJsonAdapter
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.rest.client.initializer.RestHttpClientInitializer
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.rest.token.RemoteTokenHttpRestDataSource
import com.yandex.mapkit.MapKitFactory
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class CustomApplication : Application() {
    companion object {
        const val TAG = "CustomApplication"
    }

    private lateinit var mHttpRestApi: HttpRestApi
    val httpRestApi get() = mHttpRestApi

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

        mHttpRestApi = buildHttpApi(mDB, mLocalTokenDataStoreDataSource)

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
    ): HttpRestApi {
        val errorDataSource = LocalErrorDataSourceModule
            .provideLocalErrorDataSourceModule(database.errorDao())
        val errorJsonAdapter = ErrorJsonAdapter()

        val httpCallExecutor = HttpCallExecutor(errorDataSource, errorJsonAdapter)
        val remoteTokenHttpRestDataSource = RemoteTokenHttpRestDataSource(httpCallExecutor)

        val authorizationHttpInterceptor = AuthorizationHttpRestInterceptorModule
            .provideAuthorizationHttpRestInterceptor(
                errorDataSource, errorJsonAdapter, localTokenDataSource, remoteTokenHttpRestDataSource)

        val restHttpClientInitializer = RestHttpClientInitializer(authorizationHttpInterceptor)

        val okHttpClient = HttpClientModule.provideHttpClient(
            errorDataSource, restHttpClientInitializer)

        return HttpRestApi(okHttpClient).also {
            remoteTokenHttpRestDataSource.setHttpTokenDataSourceApi(it.tokenApi) // todo: dirty..;
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