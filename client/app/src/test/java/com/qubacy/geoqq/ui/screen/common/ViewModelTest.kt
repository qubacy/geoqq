package com.qubacy.geoqq.ui.screen.common

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.setMain
import org.junit.Rule

abstract class ViewModelTest() {
    @JvmField
    @Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    open fun setup() {
        Dispatchers.setMain(StandardTestDispatcher())
    }
}