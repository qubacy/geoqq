package com.qubacy.geoqq.ui.screen.common.chat.component.list.animator

import androidx.recyclerview.widget.RecyclerView.ViewHolder

interface ChatMessageAnimatorCallback {
    fun wasViewHolderAnimated(viewHolder: ViewHolder): Boolean
    fun setViewHolderAnimated(viewHolder: ViewHolder)
}