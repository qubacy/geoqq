package com.qubacy.geoqq.ui.application.activity._common.screen.mate.chats.component.list.item.data

import android.net.Uri
import com.qubacy.utility.baserecyclerview.item.data.BaseRecyclerViewItemData

data class MateChatItemData(
    val id: Long,
    val imageUri: Uri,
    val title: String,
    val text: String,
    val newMessageCount: Int
) : BaseRecyclerViewItemData {

}