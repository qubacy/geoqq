package com.qubacy.geoqq.ui.common.component.animatedlist.adapter

import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.qubacy.geoqq.ui.common.component.animatedlist.animator.AnimatedListItemAnimatorCallback
import com.qubacy.geoqq.ui.common.component.animatedlist.layoutmanager.AnimatedListLayoutManager

abstract class AnimatedListAdapter<ViewHolderType : ViewHolder, ItemType>(

) : RecyclerView.Adapter<ViewHolderType>(), AnimatedListItemAnimatorCallback {
    data class ItemAdapterInfo<ItemType>(
        val item: ItemType,
        var wasAnimated: Boolean = false
    )

    protected val mItemAdapterInfoList = mutableListOf<ItemAdapterInfo<ItemType>>()

    private var _mIsAutoScrollingEnabled: Boolean = true
    private val mIsAutoScrollingEnabled: Boolean get() {
        return _mIsAutoScrollingEnabled
    }

    private var mRecyclerView: RecyclerView? = null
    private var mLayoutManager: AnimatedListLayoutManager? = null

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)

        mRecyclerView = recyclerView
        mLayoutManager = recyclerView.layoutManager as AnimatedListLayoutManager

        mLayoutManager!!.setOnLayoutCompletedCallback {
            if (mIsAutoScrollingEnabled) {
                mRecyclerView!!.smoothScrollToPosition(itemCount)
            }
        }

        mRecyclerView!!.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                if (newState != RecyclerView.SCROLL_STATE_IDLE) return

                val lastVisibleItemPosition = mLayoutManager!!.findLastVisibleItemPosition()

                changeAutoScrollingFlag(lastVisibleItemPosition == itemCount - 1)
            }
        })
    }

    private fun changeAutoScrollingFlag(isEnabled: Boolean) {
        if (mRecyclerView == null || mLayoutManager == null || isEnabled == mIsAutoScrollingEnabled)
            return

        _mIsAutoScrollingEnabled = isEnabled
    }

    override fun getItemCount(): Int {
        return mItemAdapterInfoList.size
    }

    fun addItem(item: ItemType) {
        mItemAdapterInfoList.add(ItemAdapterInfo(item))

        notifyItemInserted(itemCount)
    }

    fun setItems(items: List<ItemType>) {
        val prevCount = mItemAdapterInfoList.size

        mItemAdapterInfoList.clear()

        notifyItemRangeRemoved(0, prevCount)

        for (item in items) {
            mItemAdapterInfoList.add(ItemAdapterInfo(item))
        }

        changeAutoScrollingFlag(true)
        notifyItemRangeInserted(0, itemCount)
    }

    override fun wasViewHolderAnimated(viewHolder: RecyclerView.ViewHolder): Boolean {
        val messageAdapterInfo = mItemAdapterInfoList[viewHolder.adapterPosition]

        return messageAdapterInfo.wasAnimated
    }

    override fun setViewHolderAnimated(viewHolder: RecyclerView.ViewHolder) {
        val messageAdapterInfo = mItemAdapterInfoList[viewHolder.adapterPosition]

        messageAdapterInfo.wasAnimated = true
    }
}