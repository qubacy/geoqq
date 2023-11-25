package com.qubacy.geoqq.application

import com.qubacy.geoqq.BuildConfig
import com.qubacy.geoqq.applicaion.common.Application
import com.qubacy.geoqq.application.container.TestAppContainer
import com.yandex.mapkit.MapKitFactory

class TestApplication() : Application() {
    override fun onCreate() {
        super.onCreate()

        MapKitFactory.setApiKey(BuildConfig.MAPKIT_API_KEY)
    }

    fun setAppContainer() {
        mAppContainer = TestAppContainer()
    }
}