package com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment._common.component.list.adapter.producer

import android.view.View
import android.view.ViewGroup
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment._common.component.list.item.RecyclerViewItemView
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment._common.component.list.item.data.RecyclerViewItemData

abstract class BaseItemViewProducer<
    RecyclerViewItemDataType : RecyclerViewItemData,
    RecyclerViewItemViewType
>(

) where RecyclerViewItemViewType : RecyclerViewItemView<RecyclerViewItemDataType>,
        RecyclerViewItemViewType : View
{
    abstract fun createItemView(
        parent: ViewGroup, viewType: Int
    ): RecyclerViewItemViewType
}