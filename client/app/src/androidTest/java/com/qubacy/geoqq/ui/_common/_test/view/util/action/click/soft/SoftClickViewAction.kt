package com.qubacy.geoqq.ui._common._test.view.util.action.click.soft

import android.view.View
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.matcher.ViewMatchers
import org.hamcrest.Matcher

class SoftClickViewAction : ViewAction {
    override fun getDescription(): String = String()

    override fun getConstraints(): Matcher<View> {
        return ViewMatchers.isClickable()
    }

    override fun perform(uiController: UiController?, view: View?) {
        if (view == null) throw IllegalArgumentException()

        view.performClick()
    }
}