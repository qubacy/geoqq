package com.qubacy.geoqq.ui.common.component.combobox.adapter

import android.content.Context
import android.widget.ArrayAdapter
import android.widget.Filter
import androidx.annotation.LayoutRes

class ComboBoxAdapter<T>(
    context: Context,
    @LayoutRes itemLayoutResId: Int,
    items: Array<T>
) : ArrayAdapter<T>(context, itemLayoutResId, items) {

    private class TransparentFilter<T>(private val mSourceItems: Array<T>) : Filter() {
        override fun performFiltering(p0: CharSequence?): FilterResults {
            return FilterResults().apply {
                values = mSourceItems
                count = mSourceItems.size
            }
        }

        override fun publishResults(p0: CharSequence?, p1: FilterResults?) { }
    }

    private val mItemList: Array<T> = items
    private val mFilter: Filter by lazy {
        TransparentFilter<T>(items)
    }

    override fun getFilter(): Filter {
        return mFilter
    }
}