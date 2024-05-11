package com.qubacy.geoqq.ui.application

import android.app.Application
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.qubacy.geoqq.BuildConfig
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.rest.api.HttpRestApi
import com.qubacy.geoqq.data._common.repository._common.source.local.database._common.Database
import com.qubacy.geoqq.data._common.repository._common.source.local.database.error.LocalErrorDataSource
import com.qubacy.geoqq.data._common.repository._common.source.local.datastore.token.LocalTokenDataStoreDataSource
import com.qubacy.geoqq.data._common.repository._common.source.local.datastore.token.tokenDataStore
import com.qubacy.geoqq.data._common.repository._common.source.remote.http._common.client.module.HttpClientModule
import com.qubacy.geoqq.data._common.repository._common.source.remote.http._common.executor.HttpCallExecutor
import com.qubacy.geoqq.data._common.repository._common.source.remote.http._common.response.error.json.adapter.ErrorJsonAdapter
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.rest.client.interceptor.auth.AuthorizationHttpRestInterceptor
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.rest.token.RemoteTokenHttpRestDataSource
import com.yandex.mapkit.MapKitFactory
import dagger.hilt.android.HiltAndroidApp
import okhttp3.OkHttpClient

@HiltAndroidApp
class CustomApplication : Application() {
    companion object {
        const val TAG = "CustomApplication"
    }

    private lateinit var mHttpClient: OkHttpClient
    val httpClient get() = mHttpClient

    private lateinit var mHttpRestApi: HttpRestApi
    val httpRestApi get() = mHttpRestApi

    private lateinit var mDB: Database
    val db get() = mDB

    private lateinit var mLocalErrorDataSource: LocalErrorDataSource
    val localErrorDataSource get() = mLocalErrorDataSource

    private lateinit var mLocalTokenDataStoreDataSource: LocalTokenDataStoreDataSource
    val localTokenDataStoreDataSource get() = mLocalTokenDataStoreDataSource

    private val mSettingsDataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
    val settingsDataStore get() = mSettingsDataStore

    override fun onCreate() {
        super.onCreate()

        initLocals()
        initRemotes()

        MapKitFactory.setApiKey(BuildConfig.MAPKIT_API_KEY)
    }

    private fun initLocals() {
        mDB = buildDatabase()

        initSharedLocalDataSources(mDB)
    }

    private fun buildDatabase(): Database {
        return Room.databaseBuilder(
            this, Database::class.java, Database.DATABASE_NAME)
            .fallbackToDestructiveMigration()
            .createFromAsset(Database.DATABASE_NAME)
            .build()
    }

    private fun initSharedLocalDataSources(database: Database) {
        val errorDataSource = database.errorDao()
        val tokenDataStore = this.tokenDataStore

        mLocalErrorDataSource = LocalErrorDataSource(errorDataSource)
        mLocalTokenDataStoreDataSource = LocalTokenDataStoreDataSource(tokenDataStore)
    }

    private fun initRemotes() {
        val errorJsonAdapter = ErrorJsonAdapter()

        mHttpClient = buildHttpClient(mLocalErrorDataSource)
        mHttpRestApi = buildHttpRestApi(mHttpClient, errorJsonAdapter)
    }

    private fun buildHttpClient(
        localErrorDataSource: LocalErrorDataSource
    ): OkHttpClient {
        return HttpClientModule.provideHttpClient(localErrorDataSource)
    }

    private fun buildHttpRestApi(
        okHttpClient: OkHttpClient,
        errorJsonAdapter: ErrorJsonAdapter
    ): HttpRestApi {
        val httpCallExecutor = HttpCallExecutor(mLocalErrorDataSource, errorJsonAdapter)

        val remoteTokenHttpRestDataSource = RemoteTokenHttpRestDataSource(httpCallExecutor)

        val authorizationHttpRestInterceptor = AuthorizationHttpRestInterceptor(
            mLocalErrorDataSource,
            ErrorJsonAdapter(),
            mLocalTokenDataStoreDataSource,
            remoteTokenHttpRestDataSource
        )

        val restHttpClient = okHttpClient
            .newBuilder()
            .addInterceptor(authorizationHttpRestInterceptor)
            .build()

        return HttpRestApi(restHttpClient).apply {
            remoteTokenHttpRestDataSource.setHttpTokenDataSourceApi(this.tokenApi)
        }
    }
}