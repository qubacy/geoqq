package com.qubacy.geoqq.ui.screen.common.util

import androidx.fragment.app.Fragment
import androidx.fragment.app.testing.FragmentScenario
import androidx.lifecycle.MutableLiveData
import com.qubacy.geoqq.ui.common.fragment.waiting.model.WaitingViewModel

object FragmentTestUtil {
    fun setIsWaitingValue(
        fragmentScenario: FragmentScenario<Fragment>,
        waitingViewModel: WaitingViewModel,
        isWaiting: Boolean
    ) {
        val mIsWaitingFieldReflection = WaitingViewModel::class.java.getDeclaredField("mIsWaiting")
            .apply { isAccessible = true }

        if (waitingViewModel.isWaiting.value == true) {
            val isWaitingRef = mIsWaitingFieldReflection.get(waitingViewModel) as MutableLiveData<Boolean>

            fragmentScenario.onFragment {
                isWaitingRef.value = isWaiting
            }
        }
    }
}