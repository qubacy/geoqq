package com.qubacy.geoqq.ui.screen.geochat.chat.animator

import androidx.recyclerview.widget.RecyclerView.ViewHolder

interface ChatMessageAnimatorCallback {
    fun wasViewHolderAnimated(viewHolder: ViewHolder): Boolean
    fun setViewHolderAnimated(viewHolder: ViewHolder)
}