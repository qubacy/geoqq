package com.qubacy.geoqq.ui.application

import android.app.Application
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.qubacy.geoqq.BuildConfig
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.rest.api.HttpRestApi
import com.qubacy.geoqq.data._common.repository._common.source.local.database.error._common.LocalErrorDatabaseDataSource
import com.qubacy.geoqq.data._common.repository._common.source.local.database.error.impl.LocalErrorDatabaseDataSourceImpl
import com.qubacy.geoqq.data._common.repository._common.source.remote.http._common.client.module.HttpClientModule
import com.qubacy.geoqq.data._common.repository._common.source.remote.http._common.context.HttpContext
import com.qubacy.geoqq.data._common.repository._common.source.remote.http._common.executor.impl.HttpCallExecutorImpl
import com.qubacy.geoqq.data._common.repository._common.source.remote.http._common.response._common.json.adapter.StringJsonAdapter
import com.qubacy.geoqq.data._common.repository._common.source.remote.http._common.response.error.json.adapter.ErrorJsonAdapter
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.rest.client.interceptor.auth.impl.AuthorizationHttpRestInterceptorImpl
import com.qubacy.geoqq.data._common.repository.token.repository._common.source.remote.http.rest.impl.RemoteTokenHttpRestDataSourceImpl
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.adapter._common.listener.WebSocketListenerAdapter
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.container.WebSocketInitContainer
import com.qubacy.geoqq.data._common.repository.token.repository.impl.TokenDataRepositoryImpl
import com.qubacy.geoqq.ui.application.dependency.CustomApplicationComponent
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

    val customApplicationComponent: CustomApplicationComponent by lazy {
        DaggerCustomApplicationComponent.factory().create(applicationContext)
    }

    override fun onCreate() {
        super.onCreate()

        MapKitFactory.setApiKey(BuildConfig.MAPKIT_API_KEY)
    }

    private fun buildHttpRestApi(
        okHttpClient: OkHttpClient,
        moshi: Moshi,
        errorJsonAdapter: ErrorJsonAdapter
    ): HttpRestApi {
        val httpCallExecutor = HttpCallExecutorImpl(mLocalErrorDataSource, errorJsonAdapter)

        val remoteTokenHttpRestDataSource = RemoteTokenHttpRestDataSourceImpl(httpCallExecutor)

        val tokenDataRepository = TokenDataRepositoryImpl(
            mLocalErrorDataSource,
            mLocalTokenDataStoreDataSource,
            remoteTokenHttpRestDataSource
        )

        val authorizationHttpRestInterceptor = AuthorizationHttpRestInterceptorImpl(
            mLocalErrorDataSource,
            ErrorJsonAdapter(),
            tokenDataRepository
        )

        val restHttpClient = okHttpClient
            .newBuilder()
            .addInterceptor(authorizationHttpRestInterceptor)
            .build()

        return HttpRestApi(restHttpClient, moshi).apply {
            remoteTokenHttpRestDataSource.setHttpTokenDataSourceApi(this.tokenApi)
        }
    }

    private fun initWebSocketContainer(
        localErrorDatabaseDataSource: LocalErrorDatabaseDataSource
    ): WebSocketInitContainer {
        val listener = WebSocketListenerAdapter(localErrorDatabaseDataSource)
        val request = Request.Builder().url("ws://${HttpContext.BASE_HOST_PORT}").build() // todo: optimize!

        val webSocket = mHttpRestApi.okHttpClient.newWebSocket(request, listener)

        return WebSocketInitContainer(webSocket, listener)
    }
}