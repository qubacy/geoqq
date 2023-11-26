package com.qubacy.geoqq.ui.screen.mate.chat.list.adapter

import androidx.recyclerview.widget.RecyclerView
import com.qubacy.geoqq.ui.common.fragment.chat.component.list.adapter.ChatAdapter

class MateChatAdapter(
    private val mMateChatCallback: MateChatAdapterCallback
) : ChatAdapter(mMateChatCallback) {
    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)

        mRecyclerView!!.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                if (dy >= 0) return

                val firstVisibleItemPos = mLayoutManager!!.findFirstVisibleItemPosition()

                if (firstVisibleItemPos == 0) { mMateChatCallback.onEdgeReached() }
            }
        })
    }
}