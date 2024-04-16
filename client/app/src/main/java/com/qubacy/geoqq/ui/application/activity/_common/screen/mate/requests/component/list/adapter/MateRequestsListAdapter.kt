package com.qubacy.geoqq.ui.application.activity._common.screen.mate.requests.component.list.adapter

import com.qubacy.choosablelistviewlib.adapter.ChoosableListAdapter
import com.qubacy.choosablelistviewlib.item.ChoosableItemViewProvider
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.requests.component.list.adapter.producer.MateRequestItemViewProviderProducer
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.requests.component.list.item.MateRequestItemViewProvider
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.requests.component.list.item.data.MateRequestItemData

class MateRequestsListAdapter(
    itemViewProviderProducer: MateRequestItemViewProviderProducer
) : ChoosableListAdapter<
    MateRequestItemData,
    MateRequestItemViewProvider,
    MateRequestsListAdapter.ViewHolder
>(
    itemViewProviderProducer
) {
    class ViewHolder(
        choosableItemViewProvider: ChoosableItemViewProvider<
            MateRequestItemData, MateRequestItemViewProvider
        >
    ) : ChoosableListItemViewHolder<
        MateRequestItemData, MateRequestItemViewProvider
    >(choosableItemViewProvider) {

    }

    override fun createViewHolder(
        itemView: ChoosableItemViewProvider<MateRequestItemData, MateRequestItemViewProvider>
    ): ViewHolder {
        return ViewHolder(itemView)
    }
}