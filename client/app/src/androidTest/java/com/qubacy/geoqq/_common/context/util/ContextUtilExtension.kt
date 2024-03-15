package com.qubacy.geoqq._common.context.util

import android.content.ContentResolver
import android.content.Context
import android.net.Uri

fun Context.getUriFromResId(resId: Int): Uri {
    return Uri.Builder()
        .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
        .authority(resources.getResourcePackageName(resId))
        .appendPath(resources.getResourceTypeName(resId))
        .appendPath(resources.getResourceEntryName(resId))
        .build()
}