package com.qubacy.geoqq._common.util.context.extension

import android.content.Context
import android.net.Uri
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
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

val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")