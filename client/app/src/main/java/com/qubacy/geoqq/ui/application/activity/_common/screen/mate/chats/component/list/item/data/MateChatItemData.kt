package com.qubacy.geoqq.ui.application.activity._common.screen.mate.chats.component.list.item.data

import android.net.Uri
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment._common.component.list.item.data.RecyclerViewItemData

data class MateChatItemData(
    val id: Long,
    val imageUri: Uri,
    val title: String,
    val text: String,
    val newMessageCount: Int
) : RecyclerViewItemData {

}