package com.qubacy.geoqq.ui._common._test.view.util.action.scroll.recyclerview

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.matcher.ViewMatchers
import org.hamcrest.Matcher
import org.hamcrest.Matchers

class RecyclerViewScrollToPositionViewAction(
    val position: Int
) : ViewAction {
    override fun getDescription(): String = String()

    override fun getConstraints(): Matcher<View> {
        return Matchers.allOf(
            ViewMatchers.isDisplayed(),
            ViewMatchers.isAssignableFrom(RecyclerView::class.java)
        )
    }

    override fun perform(uiController: UiController?, view: View?) {
        if (view == null) throw IllegalArgumentException()

        view as RecyclerView

        view.scrollToPosition(position)
        uiController?.loopMainThreadUntilIdle()
    }
}