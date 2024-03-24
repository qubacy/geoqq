package com.qubacy.geoqq.data.image._common.util.resolver.extension

import android.content.ContentResolver
import android.content.ContentResolver.SCHEME_CONTENT
import android.content.ContentResolver.SCHEME_FILE
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.webkit.MimeTypeMap

fun ContentResolver.getFileExtensionByUri(uri: Uri): String {
    return when (uri.scheme) {
        SCHEME_CONTENT -> {
            val mimeType = getType(uri)

            MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType)!!
        }
        SCHEME_FILE -> {
            uri.lastPathSegment!!.split('.').last()
        }
        else -> throw IllegalStateException()
    }
}

fun ContentResolver.getImageBitmapByUri(imageUri: Uri): Bitmap? {
    openInputStream(imageUri)?.use {
        return BitmapFactory.decodeStream(it)
    }

    return null
}