package com.qubacy.geoqq.applicaion.impl

import com.qubacy.geoqq.BuildConfig
import com.qubacy.geoqq.applicaion.common.Application
import com.qubacy.geoqq.applicaion.common.container.AppContainer
import com.yandex.mapkit.MapKitFactory

class ApplicationImpl : Application() {
    override fun onCreate() {
        super.onCreate()

        mAppContainer = AppContainer(this)

        MapKitFactory.setApiKey(BuildConfig.MAPKIT_API_KEY)
    }
}