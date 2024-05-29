package com.qubacy.geoqq._common._test.runner

import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner
import com.qubacy.geoqq.ui.application.TestCustomApplication

class CustomTestRunner : AndroidJUnitRunner() {
    override fun newApplication(cl: ClassLoader?, name: String?, context: Context?): Application {
        return super.newApplication(cl, TestCustomApplication::class.java.name, context)
    }
}