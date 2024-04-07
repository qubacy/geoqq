package com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment._common.component.list._common.adapter

import android.view.View
import android.view.ViewGroup
import androidx.annotation.UiThread
import androidx.recyclerview.widget.RecyclerView
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment._common.component.list._common.adapter.producer.BaseItemViewProducer
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment._common.component.list._common.item.RecyclerViewItemView
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment._common.component.list._common.item.data.RecyclerViewItemData

/**
 * Note: In order to successfully compose Unit tests, it's recommended to use WRAPPED version
 * of notify..() methods.
 */
abstract class BaseRecyclerViewAdapter<
    RecyclerViewItemDataType : RecyclerViewItemData,
    RecyclerViewItemViewType,
    RecyclerViewItemViewProducerType: BaseItemViewProducer<
            RecyclerViewItemDataType, RecyclerViewItemViewType
            >,
    ViewHolderType: BaseRecyclerViewAdapter.ViewHolder<
            RecyclerViewItemDataType, RecyclerViewItemViewType
            >
>(
    val itemViewProducer: RecyclerViewItemViewProducerType
) : RecyclerView.Adapter<ViewHolderType>()
    where RecyclerViewItemViewType : RecyclerViewItemView<RecyclerViewItemDataType>,
          RecyclerViewItemViewType : View
{
    open class ViewHolder<
        RecyclerViewItemDataType : RecyclerViewItemData,
        RecyclerViewItemViewType
    >(
        val baseItemView: RecyclerViewItemViewType
    ) : RecyclerView.ViewHolder(baseItemView)
        where RecyclerViewItemViewType : RecyclerViewItemView<RecyclerViewItemDataType>,
              RecyclerViewItemViewType : View
    {
        fun setData(data: RecyclerViewItemDataType) {
            baseItemView.setData(data)
        }
    }

    companion object {
        const val TAG = "BaseRecyclerViewAdapter"
    }


    protected val mItems: MutableList<RecyclerViewItemDataType> = mutableListOf()
    val items: List<RecyclerViewItemDataType> get() = mItems

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderType {
        val itemView = itemViewProducer.createItemView(parent, viewType)

        return createViewHolder(itemView)
    }

    abstract fun createViewHolder(itemView: RecyclerViewItemViewType): ViewHolderType

    override fun onBindViewHolder(holder: ViewHolderType, position: Int) {
        val itemData = mItems[position]

        holder.baseItemView.setData(itemData)
    }

    override fun getItemCount(): Int {
        return mItems.size
    }

    /**
     * Used for detecting reaching the end of the list;
     */
    open fun getEndPosition(): Int {
        return mItems.size - 1
    }

    protected fun replaceItems(items: List<RecyclerViewItemDataType>) {
        mItems.apply {
            clear()
            addAll(items)
        }
    }

    @UiThread
    fun resetItems() {
        wrappedNotifyItemRangeRemoved(0, mItems.size)
        mItems.clear()
    }

    open fun wrappedNotifyDataSetChanged() {
        notifyDataSetChanged()
    }

    open fun wrappedNotifyItemInserted(position: Int) {
        notifyItemInserted(position)
    }

    open fun wrappedNotifyItemRemoved(position: Int) {
        notifyItemRemoved(position)
    }

    open fun wrappedNotifyItemChanged(position: Int) {
        notifyItemChanged(position)
    }

    open fun wrappedNotifyItemMoved(fromPosition: Int, toPosition: Int) {
        notifyItemMoved(fromPosition, toPosition)
    }

    open fun wrappedNotifyItemRangeChanged(positionStart: Int, itemCount: Int) {
        notifyItemRangeChanged(positionStart, itemCount)
    }

    open fun wrappedNotifyItemRangeRemoved(positionStart: Int, itemCount: Int) {
        notifyItemRangeRemoved(positionStart, itemCount)
    }

    open fun wrappedNotifyItemRangeInserted(positionStart: Int, itemCount: Int) {
        notifyItemRangeInserted(positionStart, itemCount)
    }
}