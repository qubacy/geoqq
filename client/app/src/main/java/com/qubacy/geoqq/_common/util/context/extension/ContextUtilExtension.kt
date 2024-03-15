package com.qubacy.geoqq._common.util.context.extension

import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.TypedValue
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import java.io.FileNotFoundException
import java.io.InputStream
import kotlin.Exception

fun Context.checkUriValidity(uri: Uri): Boolean {
    var inputStream: InputStream? = null

    try {
        inputStream = contentResolver.openInputStream(uri)

        return true
    }
    catch (e: Exception) { return false }
    finally { inputStream?.close() }
}

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

val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")