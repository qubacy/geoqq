package com.qubacy.geoqq.ui.application

import android.app.Application
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.qubacy.geoqq.BuildConfig
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.rest.api.HttpRestApi
import com.qubacy.geoqq.data._common.repository._common.source.local.database._common.Database
import com.qubacy.geoqq.data._common.repository._common.source.local.database.error._common.LocalErrorDatabaseDataSource
import com.qubacy.geoqq.data._common.repository._common.source.local.database.error.impl.LocalErrorDatabaseDataSourceImpl
import com.qubacy.geoqq.data._common.repository._common.source.local.datastore.token._common.tokenDataStore
import com.qubacy.geoqq.data._common.repository._common.source.local.datastore.token.impl.LocalTokenDataStoreDataSourceImpl
import com.qubacy.geoqq.data._common.repository._common.source.remote.http._common.client.module.HttpClientModule
import com.qubacy.geoqq.data._common.repository._common.source.remote.http._common.context.HttpContext
import com.qubacy.geoqq.data._common.repository._common.source.remote.http._common.executor.HttpCallExecutor
import com.qubacy.geoqq.data._common.repository._common.source.remote.http._common.response._common.json.adapter.StringJsonAdapter
import com.qubacy.geoqq.data._common.repository._common.source.remote.http._common.response.error.json.adapter.ErrorJsonAdapter
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.rest.client.interceptor.auth.AuthorizationHttpRestInterceptor
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.rest.token.impl.RemoteTokenHttpRestDataSourceImpl
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.adapter.WebSocketAdapter
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.adapter.listener.WebSocketListenerAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.yandex.mapkit.MapKitFactory
import dagger.hilt.android.HiltAndroidApp
import okhttp3.OkHttpClient
import okhttp3.Request

@HiltAndroidApp
class CustomApplication : Application() {
    companion object {
        const val TAG = "CustomApplication"
    }

    private lateinit var mHttpClient: OkHttpClient
    val httpClient get() = mHttpClient

    private lateinit var mMoshi: Moshi
    val moshi get() = mMoshi

    private lateinit var mHttpRestApi: HttpRestApi
    val httpRestApi get() = mHttpRestApi

    private val mWebSocketAdapter: WebSocketAdapter by lazy {
        initWebSocketAdapter(mLocalErrorDataSource)
    }
    val webSocketAdapter get() = mWebSocketAdapter

    private lateinit var mDB: Database
    val db get() = mDB

    private lateinit var mLocalErrorDataSource: LocalErrorDatabaseDataSourceImpl
    val localErrorDataSource get() = mLocalErrorDataSource

    private lateinit var mLocalTokenDataStoreDataSource: LocalTokenDataStoreDataSourceImpl
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

        mLocalErrorDataSource = LocalErrorDatabaseDataSourceImpl(errorDataSource)
        mLocalTokenDataStoreDataSource = LocalTokenDataStoreDataSourceImpl(tokenDataStore)
    }

    private fun initRemotes() {
        val errorJsonAdapter = ErrorJsonAdapter()

        mHttpClient = buildHttpClient(mLocalErrorDataSource)
        mMoshi = buildMoshi()
        mHttpRestApi = buildHttpRestApi(mHttpClient, mMoshi, errorJsonAdapter)
    }

    private fun buildHttpClient(
        localErrorDataSource: LocalErrorDatabaseDataSourceImpl
    ): OkHttpClient {
        return HttpClientModule.provideHttpClient(localErrorDataSource)
    }

    private fun buildMoshi(): Moshi {
        return Moshi.Builder()
            .addLast(KotlinJsonAdapterFactory())
            .add(String::class.java, StringJsonAdapter())
            .build()
    }

    private fun buildHttpRestApi(
        okHttpClient: OkHttpClient,
        moshi: Moshi,
        errorJsonAdapter: ErrorJsonAdapter
    ): HttpRestApi {
        val httpCallExecutor = HttpCallExecutor(mLocalErrorDataSource, errorJsonAdapter)

        val remoteTokenHttpRestDataSource = RemoteTokenHttpRestDataSourceImpl(httpCallExecutor)

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

        return HttpRestApi(restHttpClient, moshi).apply {
            remoteTokenHttpRestDataSource.setHttpTokenDataSourceApi(this.tokenApi)
        }
    }

    private fun initWebSocketAdapter(
        localErrorDatabaseDataSource: LocalErrorDatabaseDataSource
    ): WebSocketAdapter {
        val listener = WebSocketListenerAdapter(localErrorDatabaseDataSource)
        val request = Request.Builder().url("ws://${HttpContext.BASE_HOST_PORT}").build() // todo: optimize!

        val webSocket = mHttpRestApi.okHttpClient.newWebSocket(request, listener)

        return WebSocketAdapter(webSocket, listener).also {
            listener.setCallback(it)
        }
    }
}