package com.qubacy.geoqq.application

import com.qubacy.geoqq.applicaion.common.Application
import com.qubacy.geoqq.application.container.TestAppContainer

class TestApplication() : Application() {
    override fun onCreate() {
        super.onCreate()
    }

    fun setAppContainer() {
        mAppContainer = TestAppContainer()
    }
}