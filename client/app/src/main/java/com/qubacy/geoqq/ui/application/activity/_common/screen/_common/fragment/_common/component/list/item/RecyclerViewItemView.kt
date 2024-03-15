package com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment._common.component.list.item

import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment._common.component.list.item.data.RecyclerViewItemData

interface RecyclerViewItemView<RecyclerViewItemDataType : RecyclerViewItemData> {
    fun setData(data: RecyclerViewItemDataType)
}