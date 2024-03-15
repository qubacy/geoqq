package com.qubacy.geoqq.ui._common._test.view.util.matcher.button.navigation

import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.annotation.IdRes
import androidx.appcompat.widget.Toolbar
import org.hamcrest.BaseMatcher
import org.hamcrest.Description

class NavigationButtonViewMatcher(
    @IdRes private val mAppBarId: Int
) : BaseMatcher<View>() {
    companion object {
        const val CHILDREN_INDEX = 0
    }

    override fun describeTo(description: Description?) {}

    override fun matches(item: Any?): Boolean {
        if (item !is ImageButton) return false

        val imageButton = item as ImageButton
        val parent = (imageButton.parent as ViewGroup)

        if (parent !is Toolbar) return false
        if (parent.id != mAppBarId) return false

        val childIndex = parent.indexOfChild(imageButton)

        return (childIndex == CHILDREN_INDEX)
    }

    override fun describeMismatch(item: Any?, mismatchDescription: Description?) {

    }
}