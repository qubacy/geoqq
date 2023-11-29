package com.qubacy.geoqq.ui.common.component.animatedlist.adapter

import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.qubacy.geoqq.ui.common.component.animatedlist.animator.AnimatedListItemAnimatorCallback
import com.qubacy.geoqq.ui.common.component.animatedlist.layoutmanager.AnimatedListLayoutManager

abstract class AnimatedListAdapter<ViewHolderType : ViewHolder, ItemType>(
    private val mIsReversed: Boolean = false,
    private val mAnimatedListAdapterCallback: AnimatedListAdapterCallback? = null
) : RecyclerView.Adapter<ViewHolderType>(), AnimatedListItemAnimatorCallback {
    private enum class CallbackOperation {
        ADD_PRECEDING_ITEMS(), ADD_NEW_ITEMS(), SET_ITEMS();
    }

    data class ItemAdapterInfo<ItemType>(
        var item: ItemType,
        var wasAnimated: Boolean = false
    )

    companion object {
        const val TAG = "AnimatedListAdapter"
    }

    protected val mItemAdapterInfoList = mutableListOf<ItemAdapterInfo<ItemType>>()

    private var _mIsAutoScrollingEnabled: Boolean = true
    private val mIsAutoScrollingEnabled: Boolean get() {
        return _mIsAutoScrollingEnabled
    }

    protected var mRecyclerView: RecyclerView? = null
    protected var mLayoutManager: AnimatedListLayoutManager? = null

    private val mPendingCallbackOperations = mutableListOf<CallbackOperation>()

    private fun onLayoutCompleted() {
        if (mPendingCallbackOperations.isEmpty()) return

        Log.d(TAG, "onLayoutCompleted(): mIsAutoScrollingEnabled = $mIsAutoScrollingEnabled")

        val curPendingCallbackOperation = mPendingCallbackOperations.removeFirst()

        when (curPendingCallbackOperation) {
            CallbackOperation.SET_ITEMS -> { scrollToLastPos() }
            CallbackOperation.ADD_NEW_ITEMS -> { if (mIsAutoScrollingEnabled) scrollToLastPos() }
            CallbackOperation.ADD_PRECEDING_ITEMS -> {
                // nothing?..
            }
        }
    }

    private fun scrollToLastPos() {
        mRecyclerView!!.smoothScrollToPosition(if (mIsReversed) 0 else itemCount)

        mAnimatedListAdapterCallback?.onScrolledToLastPos()
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)

        mRecyclerView = recyclerView
        mLayoutManager = recyclerView.layoutManager as AnimatedListLayoutManager

        mLayoutManager!!.setOnLayoutCompletedCallback { onLayoutCompleted() }

        mRecyclerView!!.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                if (newState != RecyclerView.SCROLL_STATE_IDLE) return

                var isScrollEnabled = if (mIsReversed) {
                    val firstVisiblePosition = mLayoutManager!!.findFirstVisibleItemPosition()

                    firstVisiblePosition == 0

                } else {
                    val lastVisibleItemPosition = mLayoutManager!!.findLastVisibleItemPosition()

                    lastVisibleItemPosition == itemCount - 1
                }

                Log.d(TAG, "onScrollStateChanged(): isScrollEnabled = $isScrollEnabled")

                changeAutoScrollingFlag(isScrollEnabled)
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
        val insertedItemPos = processAddItem(item)

        mPendingCallbackOperations.add(CallbackOperation.ADD_NEW_ITEMS)
        notifyItemInserted(insertedItemPos)
    }

    protected open fun processAddItem(item: ItemType): Int {
        if (mIsReversed)
            mItemAdapterInfoList.add(0, ItemAdapterInfo(item))
        else
            mItemAdapterInfoList.add(ItemAdapterInfo(item))

        return (if (mIsReversed) 0 else itemCount)
    }

    fun addPrecedingItems(precedingItems: List<ItemType>) {
        mItemAdapterInfoList.addAll(0,
            precedingItems.map { ItemAdapterInfo(it, true) })

        mPendingCallbackOperations.add(CallbackOperation.ADD_PRECEDING_ITEMS)
        notifyItemRangeInserted(0, precedingItems.size)
    }

    fun setItems(items: List<ItemType>) {
        val prevCount = mItemAdapterInfoList.size

        mItemAdapterInfoList.clear()

        notifyItemRangeRemoved(0, prevCount)

        for (item in items) {
            mItemAdapterInfoList.add(ItemAdapterInfo(item))
        }

        changeAutoScrollingFlag(true)
        mPendingCallbackOperations.add(CallbackOperation.SET_ITEMS)
        notifyItemRangeInserted(0, itemCount)
    }

    fun updateItem(item: ItemType) {
        val changedItemPos = changeItem(item)

        notifyItemChanged(changedItemPos) // todo: should it be here?
    }

    protected open fun changeItem(item: ItemType): Int {
        val itemPosAndInfo = getItemInfoAndPosForItem(item)

        if (itemPosAndInfo == null)
            throw IllegalArgumentException()

        itemPosAndInfo.second.item = item

        return itemPosAndInfo.first
    }

    protected fun getItemInfoAndPosForItem(item: ItemType): Pair<Int, ItemAdapterInfo<ItemType>>? {
        var defaultItemInfo: ItemAdapterInfo<ItemType>? = null
        var defaultItemPos: Int = -1

        for (pos in 0 until mItemAdapterInfoList.size) {
            val curItemInfo = mItemAdapterInfoList[pos]

            if (curItemInfo.item == item) {
                defaultItemInfo = curItemInfo
                defaultItemPos = pos

                break
            }
        }

        if (defaultItemInfo == null) return null

        return Pair(defaultItemPos, defaultItemInfo)
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