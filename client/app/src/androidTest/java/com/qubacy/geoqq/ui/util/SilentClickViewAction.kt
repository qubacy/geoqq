package com.qubacy.geoqq.ui.util

import android.view.View
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.matcher.ViewMatchers
import org.hamcrest.Matcher
import org.hamcrest.Matchers

class SilentClickViewAction() : ViewAction {
    override fun getDescription(): String {
        return "Just a programmatic click!"
    }

    override fun getConstraints(): Matcher<View> {
        return Matchers.allOf()
    }

    override fun perform(uiController: UiController?, view: View?) {
        if (view == null)
            throw IllegalArgumentException()

        view.performClick()
    }
}