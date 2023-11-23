package com.qubacy.geoqq.ui.screen.common.fragment.common

import androidx.test.core.app.ApplicationProvider
import com.qubacy.geoqq.application.TestApplication

abstract class FragmentTestBase {
    open fun setup() {
        val app = ApplicationProvider.getApplicationContext<TestApplication>()

        app.setAppContainer()
    }
}