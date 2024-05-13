package com.qubacy.geoqq.ui.application.activity._common.screen.geo.chat.presentation

import android.os.Parcel
import android.os.Parcelable
import androidx.core.os.ParcelCompat
import com.qubacy.geoqq.domain.geo._common.model.GeoMessage
import com.qubacy.geoqq.ui._common.util.time.TimeUtils
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.chat.component.list.item.data.side.SenderSide
import com.qubacy.geoqq.ui.application.activity._common.screen._common.presentation.user.UserPresentation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.presentation.user.toUserPresentation
import com.qubacy.geoqq.ui.application.activity._common.screen.geo.chat.component.list.item.data.GeoMessageItemData
import java.util.Locale
import java.util.TimeZone

data class GeoMessagePresentation(
    val id: Long,
    val user: UserPresentation,
    val text: String,
    val timestamp: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        ParcelCompat.readParcelable(
            parcel,
            UserPresentation::class.java.classLoader,
            UserPresentation::class.java
        )!!,
        parcel.readString()!!,
        parcel.readString()!!
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeParcelable(user, flags)
        parcel.writeString(text)
        parcel.writeString(timestamp)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<GeoMessagePresentation> {
        override fun createFromParcel(parcel: Parcel): GeoMessagePresentation {
            return GeoMessagePresentation(parcel)
        }

        override fun newArray(size: Int): Array<GeoMessagePresentation?> {
            return arrayOfNulls(size)
        }
    }
}

fun GeoMessage.toGeoMessagePresentation(): GeoMessagePresentation {
    val timestamp = TimeUtils.longToHoursMinutesSecondsFormattedString(
        time, Locale.getDefault(), TimeZone.getDefault())

    return GeoMessagePresentation(id, user.toUserPresentation(), text, timestamp)
}

fun GeoMessagePresentation.toGeoMessageItemData(localUserId: Long): GeoMessageItemData {
    val senderSide = if (localUserId == user.id) SenderSide.ME else SenderSide.OTHER

    return GeoMessageItemData(id, senderSide, text, timestamp, user.username)
}