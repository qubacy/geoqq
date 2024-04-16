package com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment._common.component.list._common.item

import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment._common.component._common.view.provider.ViewProvider
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment._common.component.list._common.item.data.RecyclerViewItemData

interface RecyclerViewItemViewProvider<RecyclerViewItemDataType : RecyclerViewItemData> : ViewProvider {
    fun setData(data: RecyclerViewItemDataType)
}