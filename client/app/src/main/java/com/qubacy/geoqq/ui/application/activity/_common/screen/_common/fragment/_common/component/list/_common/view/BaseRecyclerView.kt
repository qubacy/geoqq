package com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment._common.component.list._common.view

import android.content.Context
import android.util.AttributeSet
import androidx.annotation.CallSuper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment._common.component.list._common.adapter.BaseRecyclerViewAdapter

class BaseRecyclerView(
    context: Context,
    attrs: AttributeSet
) : RecyclerView(context, attrs) {
    private var mCallback: BaseRecyclerViewCallback? = null

    private var mIsEndReached: Boolean = false

    fun setCallback(callback: BaseRecyclerViewCallback) {
        mCallback = callback
    }

    override fun onScrolled(dx: Int, dy: Int) {
        super.onScrolled(dx, dy)

        checkIsEndReached()
    }

    @CallSuper
    protected open fun checkIsEndReached(): Boolean {
        val layoutManager = layoutManager
        val adapter = adapter

        if (mCallback == null || layoutManager == null
        || adapter == null || adapter !is BaseRecyclerViewAdapter<*, *, *, *>
        )
        {
            return true
        }

        when (layoutManager::class) {
            LinearLayoutManager::class ->
                checkLinearLayoutManagerForEndReach(layoutManager as LinearLayoutManager, adapter)
            else -> return false
        }

        return true
    }

    private fun checkLinearLayoutManagerForEndReach(
        linearLayoutManager: LinearLayoutManager,
        adapter: BaseRecyclerViewAdapter<*, *, *, *>
    ) {
        val lastVisiblePosition = linearLayoutManager.findLastVisibleItemPosition()
        val endPosition = adapter.getEndPosition()

        checkIsEndReachedByPositions(lastVisiblePosition, endPosition)
    }

    protected fun checkIsEndReachedByPositions(
        lastVisiblePosition: Int,
        endPosition: Int
    ) {
        if (lastVisiblePosition == endPosition) {
            if (mIsEndReached) return

            mIsEndReached = true

            mCallback!!.onEndReached()

        } else {
            if (mIsEndReached) mIsEndReached = false
        }
    }
}