package com.qubacy.geoqq.common

import androidx.test.core.app.ApplicationProvider
import com.qubacy.geoqq.application.TestApplication

abstract class ApplicationTestBase {
    open fun setup() {
        val app = ApplicationProvider.getApplicationContext<TestApplication>()

        app.setAppContainer()
    }
}