package com.qubacy.geoqq

import android.app.Application
import com.yandex.mapkit.MapKitFactory

class Application : Application() {
    override fun onCreate() {
        super.onCreate()

        MapKitFactory.setApiKey(BuildConfig.MAPKIT_API_KEY)
    }
}