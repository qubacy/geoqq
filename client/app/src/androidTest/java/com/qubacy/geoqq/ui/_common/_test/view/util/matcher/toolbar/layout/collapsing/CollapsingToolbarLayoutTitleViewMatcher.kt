package com.qubacy.geoqq.ui._common._test.view.util.matcher.toolbar.layout.collapsing

import android.view.View
import com.google.android.material.appbar.CollapsingToolbarLayout
import org.hamcrest.BaseMatcher
import org.hamcrest.Description

class CollapsingToolbarLayoutTitleViewMatcher(
    val title: String
) : BaseMatcher<View>() {
    override fun describeTo(description: Description?) {  }

    override fun matches(item: Any?): Boolean {
        if (item == null || item !is CollapsingToolbarLayout) return false

        return (item.title == title)
    }
}