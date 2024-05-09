package com.qubacy.geoqq.ui._common._test.view.util.matcher.image.common

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.View
import android.widget.ImageView
import androidx.test.platform.app.InstrumentationRegistry
import com.qubacy.geoqq.ui._common.util.context.extension.getDrawableFromUri
import org.hamcrest.BaseMatcher
import org.hamcrest.Description

// todo: doesn't work for now. fix it!
class CommonImageViewMatcher : BaseMatcher<View> {
    companion object {
        const val BITMAP_SIZE = 64
    }

    private val mImage: Drawable

    constructor(image: Drawable) : super() {
        mImage = image
    }

    constructor(imageUri: Uri): super() {
        mImage = InstrumentationRegistry.getInstrumentation().targetContext
            .getDrawableFromUri(imageUri)!!
    }

    override fun describeTo(description: Description?) { }

    override fun matches(item: Any?): Boolean {
        if (item !is ImageView) return false

        val imageBitmap = getBitmap(mImage)
        val itemImageBitmap = getBitmap(item.drawable)

        return (itemImageBitmap.sameAs(imageBitmap))
    }

    private fun getBitmap(drawable: Drawable): Bitmap {
        val bitmap = Bitmap.createBitmap(BITMAP_SIZE, BITMAP_SIZE, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)

        return bitmap
    }
}