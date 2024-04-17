package com.qubacy.geoqq.ui.application.activity._common.screen.mate.requests.component.list.item.data

import android.net.Uri
import com.qubacy.utility.baserecyclerview.item.data.BaseRecyclerViewItemData

data class MateRequestItemData(
    val id: Long,
    val imageUri: Uri,
    val username: String
) : BaseRecyclerViewItemData {

}