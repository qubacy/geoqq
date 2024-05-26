package com.qubacy.geoqq.ui.application

import android.app.Application
import com.qubacy.geoqq.BuildConfig
import com.qubacy.geoqq.ui._di.component.CustomApplicationComponent
import com.yandex.mapkit.MapKitFactory

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
}