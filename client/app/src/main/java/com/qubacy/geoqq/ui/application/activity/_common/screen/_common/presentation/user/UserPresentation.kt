package com.qubacy.geoqq.ui.application.activity._common.screen._common.presentation.user

import android.os.Parcel
import android.os.Parcelable
import androidx.core.os.ParcelCompat
import com.qubacy.geoqq.domain._common.model.user.User
import com.qubacy.geoqq.ui.application.activity._common.screen._common.presentation.image.ImagePresentation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.presentation.image.toImagePresentation

data class UserPresentation(
    val id: Long,
    val username: String,
    val description: String?,
    val avatar: ImagePresentation,
    val isMate: Boolean,
    val isDeleted: Boolean
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readString()!!,
        parcel.readString(),
        ParcelCompat.readParcelable(
            parcel,
            ImagePresentation::class.java.classLoader,
            ImagePresentation::class.java
        )!!,
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeString(username)
        parcel.writeString(description)
        parcel.writeByte(if (isMate) 1 else 0)
        parcel.writeByte(if (isDeleted) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<UserPresentation> {
        override fun createFromParcel(parcel: Parcel): UserPresentation {
            return UserPresentation(parcel)
        }

        override fun newArray(size: Int): Array<UserPresentation?> {
            return arrayOfNulls(size)
        }
    }

}

fun User.toUserPresentation(): UserPresentation {
    return UserPresentation(
        id,
        username,
        description,
        avatar.toImagePresentation(),
        isMate,
        isDeleted
    )
}