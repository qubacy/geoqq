package com.qubacy.geoqq.ui._common._test.view.util.matcher.toast.root

import android.view.View
import androidx.fragment.app.FragmentActivity
import org.hamcrest.BaseMatcher
import org.hamcrest.Description

class ToastRootMatcher(
    val activity: FragmentActivity
) : BaseMatcher<View>() {
    override fun describeTo(description: Description?) { }

    override fun matches(item: Any?): Boolean {
        if (item == null) return false

        return activity.window.decorView != item
    }
}