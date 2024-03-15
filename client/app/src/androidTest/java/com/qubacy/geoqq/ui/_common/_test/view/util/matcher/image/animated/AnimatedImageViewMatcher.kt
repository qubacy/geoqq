package com.qubacy.geoqq.ui._common._test.view.util.matcher.image.animated

import android.view.View
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.core.graphics.drawable.toBitmap
import androidx.test.platform.app.InstrumentationRegistry
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import org.hamcrest.BaseMatcher
import org.hamcrest.Description

class AnimatedImageViewMatcher(
    @DrawableRes private val mImageResId: Int
) : BaseMatcher<View>() {
    override fun describeTo(description: Description?) { }

    override fun matches(item: Any?): Boolean {
        if (item !is ImageView) return false

        item as ImageView

        val context = InstrumentationRegistry.getInstrumentation().targetContext

        val imageBitmap = AnimatedVectorDrawableCompat.create(context, mImageResId)!!.toBitmap()
        val itemImageBitmap = (item.drawable as AnimatedVectorDrawableCompat).toBitmap()

        return (itemImageBitmap.sameAs(imageBitmap))
    }
}