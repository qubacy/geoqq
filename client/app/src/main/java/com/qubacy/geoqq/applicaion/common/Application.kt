package com.qubacy.geoqq.applicaion.common

import android.app.Application
import com.qubacy.geoqq.applicaion.common.container.AppContainer

abstract class Application : Application() {
    protected lateinit var mAppContainer: AppContainer
    val appContainer: AppContainer get() = mAppContainer
}