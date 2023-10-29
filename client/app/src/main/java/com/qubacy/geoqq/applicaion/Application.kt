package com.qubacy.geoqq.applicaion

import android.app.Application
import com.qubacy.geoqq.BuildConfig
import com.qubacy.geoqq.applicaion.container.AppContainer
import com.yandex.mapkit.MapKitFactory

class Application : Application() {
    lateinit var appContainer: AppContainer

    override fun onCreate() {
        super.onCreate()

        appContainer = AppContainer(this)

        MapKitFactory.setApiKey(BuildConfig.MAPKIT_API_KEY)
    }
}