package com.qubacy.geoqq.ui.application.activity._common.screen._common.presentation.image

import android.net.Uri
import android.os.Parcel
import android.os.Parcelable
import androidx.core.os.ParcelCompat
import com.qubacy.geoqq.domain._common.model.image.Image

data class ImagePresentation(
    val id: Long,
    val uri: Uri
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        ParcelCompat.readParcelable(
            parcel,
            Uri::class.java.classLoader,
            Uri::class.java
        )!!
    ) {
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeLong(id)
        dest.writeParcelable(uri, flags)
    }

    companion object CREATOR : Parcelable.Creator<ImagePresentation> {
        override fun createFromParcel(parcel: Parcel): ImagePresentation {
            return ImagePresentation(parcel)
        }

        override fun newArray(size: Int): Array<ImagePresentation?> {
            return arrayOfNulls(size)
        }
    }
}

fun Image.toImagePresentation(): ImagePresentation {
    return ImagePresentation(id, uri)
}