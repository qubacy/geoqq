package com.qubacy.geoqq.ui.common.component.animatedlist.animator

import androidx.recyclerview.widget.RecyclerView.ViewHolder

interface AnimatedListItemAnimatorCallback {
    fun wasViewHolderAnimated(viewHolder: ViewHolder): Boolean
    fun setViewHolderAnimated(viewHolder: ViewHolder)
}