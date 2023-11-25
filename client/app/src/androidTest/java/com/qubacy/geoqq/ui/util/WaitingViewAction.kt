package com.qubacy.geoqq.ui.util

import android.view.View
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.matcher.ViewMatchers
import org.hamcrest.Matcher
import org.hamcrest.Matchers

class WaitingViewAction(
    private val duration: Long,
    private val callback: (() -> Unit)? = null
) : ViewAction {
    override fun getDescription(): String {
        return "Just waiting for a given amount of time."
    }

    override fun getConstraints(): Matcher<View> {
        return Matchers.any(View::class.java)
    }

    override fun perform(uiController: UiController?, view: View?) {
        if (uiController == null) return

        val endTime = System.currentTimeMillis() + duration

        uiController.loopMainThreadUntilIdle()

        while (System.currentTimeMillis() < endTime) {
            uiController.loopMainThreadForAtLeast(100)
        }

        callback?.let { it() }
    }
}