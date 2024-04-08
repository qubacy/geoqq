package com.qubacy.geoqq.ui.application.activity._common.screen.mate._common.presentation

import android.os.Parcel
import android.os.Parcelable
import androidx.core.os.ParcelCompat
import com.qubacy.geoqq.domain.mate.chats.model.MateChat
import com.qubacy.geoqq.ui.application.activity._common.screen._common.presentation.user.UserPresentation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.presentation.user.toUserPresentation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chats.component.list.item.data.MateChatItemData

data class MateChatPresentation(
    val id: Long,
    val user: UserPresentation,
    val newMessageCount: Int,
    val lastMessage: MateMessagePresentation?
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        ParcelCompat.readParcelable(
            parcel,
            UserPresentation::class.java.classLoader,
            UserPresentation::class.java
        )!!,
        parcel.readInt(),
        ParcelCompat.readParcelable(
            parcel,
            MateMessagePresentation::class.java.classLoader,
            MateMessagePresentation::class.java
        )
    ) {

    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeParcelable(user, flags)
        parcel.writeInt(newMessageCount)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MateChatPresentation> {
        override fun createFromParcel(parcel: Parcel): MateChatPresentation {
            return MateChatPresentation(parcel)
        }

        override fun newArray(size: Int): Array<MateChatPresentation?> {
            return arrayOfNulls(size)
        }
    }
}

fun MateChat.toMateChatPresentation(): MateChatPresentation {
    return MateChatPresentation(
        id,
        user.toUserPresentation(),
        newMessageCount,
        lastMessage?.toMateMessagePresentation()
    )
}

fun MateChatPresentation.toMateChatItemData(): MateChatItemData {
    val lastMessageText = lastMessage?.text ?: String()

    return MateChatItemData(
        id,
        user.avatar.uri,
        user.username,
        lastMessageText,
        newMessageCount
    )
}