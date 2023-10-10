package com.qubacy.geoqq.ui.util

import android.view.View
import androidx.test.espresso.NoMatchingViewException
import androidx.test.espresso.ViewAssertion

class ScrollOccurredViewAssertion : ViewAssertion {
    override fun check(view: View?, noViewFoundException: NoMatchingViewException?) {
        if (view == null)
            throw NoMatchingViewException.Builder().build()

        if (view.scrollY == 0 && view.scrollX == 0)
            throw NoMatchingViewException.Builder().build()
    }

}