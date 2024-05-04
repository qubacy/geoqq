package com.qubacy.geoqq.ui.application.activity._common.screen.mate.requests.component.list.adapter

import androidx.annotation.UiThread
import com.qubacy.choosablelistviewlib.adapter.ChoosableListAdapter
import com.qubacy.choosablelistviewlib.adapter.producer.ChoosableItemViewProviderProducer
import com.qubacy.choosablelistviewlib.item.ChoosableItemViewProvider
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.requests.component.list.adapter.producer.MateRequestItemViewProviderProducer
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.requests.component.list.item.MateRequestItemViewProvider
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.requests.component.list.item.data.MateRequestItemData

class MateRequestsListAdapter(
    itemViewProviderProducer: MateRequestItemViewProviderProducer,
    private val mCallback: MateRequestsListAdapterCallback
) : ChoosableListAdapter<
    MateRequestItemData,
    MateRequestItemViewProvider,
    ChoosableItemViewProviderProducer<MateRequestItemData, MateRequestItemViewProvider>,
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

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)

        val id = mItems[position].id

        holder.baseItemViewProvider.setOnClickListener {
            mCallback.onMateRequestClicked(id)
        }
    }

    @UiThread
    fun setMateRequests(mateRequests: List<MateRequestItemData>) {
        resetItems()

        mItems.addAll(mateRequests)

        wrappedNotifyItemRangeInserted(0, mItems.size)
    }

    @UiThread
    fun insertMateRequests(mateRequests: List<MateRequestItemData>, position: Int) {
        mItems.addAll(position, mateRequests)

        wrappedNotifyItemRangeInserted(position, mItems.size)
    }

    @UiThread
    fun addNewMateRequest(mateRequest: MateRequestItemData) {
        mItems.add(0, mateRequest)

        wrappedNotifyItemInserted(0)
    }

    @UiThread
    fun updateMateRequest(mateRequest: MateRequestItemData): Int {
        val mateRequestPosition = mItems.indexOfFirst { it.id == mateRequest.id }

        return updateMateRequestAtPosition(mateRequest, mateRequestPosition)
    }

    @UiThread
    fun updateMateRequestAtPosition(mateRequest: MateRequestItemData, position: Int): Int {
        mItems[position] = mateRequest

        wrappedNotifyItemChanged(position)

        return position
    }

    @UiThread
    fun updateMateRequests(mateRequests: List<MateRequestItemData>, position: Int) {
        for (i in position until mateRequests.size) {
            val updatedRequest = mateRequests[i - position]

            mItems[position] = updatedRequest
        }

        wrappedNotifyItemRangeChanged(position, mateRequests.size)
    }
}