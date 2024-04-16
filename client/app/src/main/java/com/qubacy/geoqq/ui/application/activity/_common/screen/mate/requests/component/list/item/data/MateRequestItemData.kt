package com.qubacy.geoqq.ui.application.activity._common.screen.mate.requests.component.list.item.data

import android.net.Uri
import com.qubacy.choosablelistviewlib.item.content.data.ChoosableItemContentViewData

data class MateRequestItemData(
    val id: Long,
    val imageUri: Uri,
    val username: String
) : ChoosableItemContentViewData {

}