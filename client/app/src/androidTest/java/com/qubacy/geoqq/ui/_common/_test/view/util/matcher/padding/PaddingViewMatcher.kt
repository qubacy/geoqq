package com.qubacy.geoqq.ui._common._test.view.util.matcher.padding

import android.view.View
import org.hamcrest.BaseMatcher
import org.hamcrest.Description

class PaddingViewMatcher(
    private val mTopPadding: Int? = null,
    private val mLeftPadding: Int? = null,
    private val mBottomPadding: Int? = null,
    private val mRightPadding: Int? = null
) : BaseMatcher<View>() {
    override fun describeTo(description: Description?) { }

    override fun matches(item: Any?): Boolean {
        if (item !is View) return false

        if (mTopPadding != null) if (item.paddingTop != mTopPadding) return false
        if (mLeftPadding != null) if (item.paddingLeft != mLeftPadding) return false
        if (mBottomPadding != null) if (item.paddingBottom != mBottomPadding) return false
        if (mRightPadding != null) if (item.paddingRight != mRightPadding) return false

        return true
    }

    override fun describeMismatch(item: Any?, mismatchDescription: Description?) { }
}