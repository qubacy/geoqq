package com.qubacy.geoqq.ui.screen.geochat.chat.layoutmanager

import android.content.Context
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class GeoChatLayoutManager(
    context: Context,
    orientation: Int,
    reverseLayout: Boolean
) : LinearLayoutManager(context, orientation, reverseLayout) {
    private var mOnLayoutCompletedCallback: (() -> Unit)? = null

    override fun onLayoutCompleted(state: RecyclerView.State?) {
        super.onLayoutCompleted(state)

        Log.d("TEST", "onLayoutCompleted")

        if (mOnLayoutCompletedCallback != null)
            mOnLayoutCompletedCallback!!()
    }

    fun setOnLayoutCompletedCallback(callback: () -> Unit) {
        mOnLayoutCompletedCallback = callback
    }

    override fun scrollToPosition(position: Int) {
        super.scrollToPosition(position)

        Log.d("TEST", "scrollToPosition: position = $position")
    }
}