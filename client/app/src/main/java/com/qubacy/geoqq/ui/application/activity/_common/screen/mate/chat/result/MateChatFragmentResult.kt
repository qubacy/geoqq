package com.qubacy.geoqq.ui.application.activity._common.screen.mate.chat.result

import android.os.Parcel
import android.os.Parcelable

data class MateChatFragmentResult(
    val chatId: Long,
    val isDeleted: Boolean = false
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readByte() != 0.toByte()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(chatId)
        parcel.writeByte(if (isDeleted) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MateChatFragmentResult> {
        override fun createFromParcel(parcel: Parcel): MateChatFragmentResult {
            return MateChatFragmentResult(parcel)
        }

        override fun newArray(size: Int): Array<MateChatFragmentResult?> {
            return arrayOfNulls(size)
        }
    }

}