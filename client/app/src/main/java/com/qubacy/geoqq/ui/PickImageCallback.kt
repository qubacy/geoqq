package com.qubacy.geoqq.ui

import android.net.Uri

interface PickImageCallback {
    fun onImagePicked(image: Uri)
    fun onImagePickingError(errorId: Long)
}