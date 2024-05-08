package com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.loading

import androidx.test.core.app.ActivityScenario
import org.junit.Test

interface LoadingFragmentTest<FragmentType : LoadingFragment> {
    @Test
    fun adjustUiWithLoadingStateTest() {
        beforeAdjustUiWithLoadingStateTest()

        val activityScenario = getLoadingFragmentActivityScenario()

        val initLoadingState = false
        val finalLoadingState = true

        activityScenario.onActivity {
            getLoadingFragmentFragment().adjustUiWithLoadingState(initLoadingState)
        }
        assertAdjustUiWithFalseLoadingState()

        activityScenario.onActivity {
            getLoadingFragmentFragment().adjustUiWithLoadingState(finalLoadingState)
        }
        assertAdjustUiWithTrueLoadingState()
    }

    fun beforeAdjustUiWithLoadingStateTest()

    fun getLoadingFragmentFragment(): FragmentType
    fun getLoadingFragmentActivityScenario(): ActivityScenario<*>

    fun assertAdjustUiWithFalseLoadingState()
    fun assertAdjustUiWithTrueLoadingState()
}