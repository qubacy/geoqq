package com.qubacy.geoqq.ui.application.activity._common.screen.mate.requests.component.list.adapter.producer

import android.content.Context
import android.view.ViewGroup
import com.google.android.material.divider.MaterialDivider
import com.qubacy.choosablelistviewlib.adapter.producer.ChoosableItemViewProviderProducer
import com.qubacy.choosablelistviewlib.item.ChoosableItemViewProvider
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.requests.component.list.item.MateRequestItemViewProvider
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.requests.component.list.item.data.MateRequestItemData

class MateRequestItemViewProviderProducer(
    context: Context
) : ChoosableItemViewProviderProducer<
    MateRequestItemData, MateRequestItemViewProvider
>(context) {
    override fun createItemView(
        parent: ViewGroup,
        viewType: Int
    ): ChoosableItemViewProvider<MateRequestItemData, MateRequestItemViewProvider> {
        val contentItemViewProvider = MateRequestItemViewProvider(parent)
        val itemView = createChoosableItemView(parent, contentItemViewProvider)

        return itemView
    }

    override fun createDivider(context: Context): MaterialDivider? {
        return MaterialDivider(context)
    }
}