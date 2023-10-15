package com.qubacy.geoqq.ui.util

import android.view.View
import android.view.ViewGroup
import androidx.annotation.IntRange
import androidx.core.view.get
import androidx.test.espresso.NoMatchingViewException
import androidx.test.espresso.ViewAssertion
import junit.framework.AssertionFailedError
import org.junit.Assert

class IsChildWithIndexViewAssertion(
    @IntRange(0) val childIndex: Int
) : ViewAssertion {
    override fun check(view: View?, noViewFoundException: NoMatchingViewException?) {
        if (view == null)
            throw IllegalArgumentException()
        if (view.parent == null)
            throw AssertionFailedError()
        if (view.parent !is ViewGroup)
            throw AssertionFailedError()

        val parentViewGroup = view.parent as ViewGroup

        if (childIndex >= parentViewGroup.childCount)
            throw AssertionFailedError()

        Assert.assertEquals(view, parentViewGroup[childIndex])
    }
}