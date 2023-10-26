package com.qubacy.geoqq.ui

import android.net.Uri
import com.qubacy.geoqq.common.error.Error

interface PickImageCallback {
    fun onImagePicked(image: Uri)
    fun onImagePickingError(error: Error)
}