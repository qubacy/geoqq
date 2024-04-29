package com.qubacy.geoqq.ui.application.activity._common.screen.mate._common.presentation

import android.os.Parcel
import android.os.Parcelable
import androidx.core.os.ParcelCompat
import com.qubacy.geoqq.domain.mate.chat.model.MateMessage
import com.qubacy.geoqq.ui._common.util.time.TimeUtils
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.chat.component.list.item.data.MessageItemData
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.chat.component.list.item.data.side.SenderSide
import com.qubacy.geoqq.ui.application.activity._common.screen._common.presentation.user.UserPresentation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.presentation.user.toUserPresentation
import java.util.Locale
import java.util.TimeZone

data class MateMessagePresentation(
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

    companion object CREATOR : Parcelable.Creator<MateMessagePresentation> {
        override fun createFromParcel(parcel: Parcel): MateMessagePresentation {
            return MateMessagePresentation(parcel)
        }

        override fun newArray(size: Int): Array<MateMessagePresentation?> {
            return arrayOfNulls(size)
        }
    }

}

fun MateMessage.toMateMessagePresentation(): MateMessagePresentation {
    val timestamp = TimeUtils.longToHoursMinutesSecondsFormattedString(
        time, Locale.getDefault(), TimeZone.getDefault())

    return MateMessagePresentation(id, user.toUserPresentation(), text, timestamp)
}

fun MateMessagePresentation.toMateMessageItemData(remoteUserId: Long): MessageItemData {
    val senderSide = if (remoteUserId == user.id) SenderSide.OTHER else SenderSide.ME

    return MessageItemData(id, senderSide, text, timestamp)
}