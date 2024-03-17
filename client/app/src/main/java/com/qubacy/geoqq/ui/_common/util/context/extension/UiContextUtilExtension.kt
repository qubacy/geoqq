package com.qubacy.geoqq.ui._common.util.context.extension

import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.TypedValue
import java.io.FileNotFoundException

fun Context.getDrawableFromUri(uri: Uri): Drawable? {
    var drawable: Drawable? = null

    try {
        val inputStream = contentResolver.openInputStream(uri)

        drawable = Drawable.createFromStream(inputStream, uri.toString())

    }
    catch (_: FileNotFoundException) { }
    catch (e: Exception) {
        e.printStackTrace()

        throw e
    }

    return drawable
}

fun Context.dpToPx(dp: Int): Int {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        dp.toFloat(),
        resources.displayMetrics
    ).toInt()
}