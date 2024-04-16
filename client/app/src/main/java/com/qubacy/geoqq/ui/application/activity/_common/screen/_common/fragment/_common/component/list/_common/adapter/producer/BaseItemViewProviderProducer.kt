package com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment._common.component.list._common.adapter.producer

import android.view.ViewGroup
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment._common.component.list._common.item.RecyclerViewItemViewProvider
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment._common.component.list._common.item.data.RecyclerViewItemData

abstract class BaseItemViewProviderProducer<
    RecyclerViewItemDataType : RecyclerViewItemData,
    RecyclerViewItemViewProviderType : RecyclerViewItemViewProvider<RecyclerViewItemDataType>
>() {
    abstract fun createItemViewProvider(
        parent: ViewGroup, viewType: Int
    ): RecyclerViewItemViewProviderType
}