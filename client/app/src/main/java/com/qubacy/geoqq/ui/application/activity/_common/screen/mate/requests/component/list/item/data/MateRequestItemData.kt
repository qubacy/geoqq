package com.qubacy.geoqq.ui.application.activity._common.screen.mate.requests.component.list.item.data

import android.net.Uri
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.requests._common.presentation.MateRequestPresentation
import com.qubacy.utility.baserecyclerview.item.data.BaseRecyclerViewItemData

data class MateRequestItemData(
    val id: Long,
    val imageUri: Uri,
    val username: String
) : BaseRecyclerViewItemData {

}

fun MateRequestPresentation.toMateRequestItemData(): MateRequestItemData {
    return MateRequestItemData(id, user.avatar.uri, user.username)
}