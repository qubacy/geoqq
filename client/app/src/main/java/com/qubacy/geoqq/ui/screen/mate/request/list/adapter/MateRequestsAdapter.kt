package com.qubacy.geoqq.ui.screen.mate.request.list.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.carousel3dlib.adapter.Carousel3DAdapter
import com.example.carousel3dlib.adapter.Carousel3DViewHolder
import com.example.carousel3dlib.general.Carousel3DContext
import com.example.carousel3dlib.layoutmanager.Carousel3DHorizontalSwipeHandler
import com.qubacy.geoqq.databinding.ComponentMateRequestBinding
import com.qubacy.geoqq.domain.mate.request.model.MateRequest

open class MateRequestsAdapter(
    private val mCallback: MateRequestsAdapterCallback
) : Carousel3DAdapter<MateRequest>() {
    companion object {
        const val TAG = "MATE_CAROUSEL_ADAPTER"
    }

    override fun areItemsExpandable(): Boolean {
        return true
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MateRequestViewHolder {
        val itemBinding = ComponentMateRequestBinding.inflate(
            LayoutInflater.from(parent.context), parent, false)

        return MateRequestViewHolder(itemBinding)
    }

    override fun getItemCount(): Int {
        return getItems().size
    }

    override fun onBindViewHolder(holder: Carousel3DViewHolder, position: Int) {
        val user = mCallback.getUserById(getItems()[position].userId)

        (holder as MateRequestViewHolder).bind(user)
    }

    override fun onHorizontalSwipe(
        position: Int,
        direction: Carousel3DContext.SwipeDirection,
        handler: Carousel3DHorizontalSwipeHandler
    ) {
        val curItem = getItems()[position]

        if (curItem == null)
            throw IllegalStateException("Current item hasn't been found!")

        mCallback.onMateRequestSwiped(position, curItem, direction)

        // Providing an answer to the request by calling the following method on the handler:

        handler.onHorizontalSwipeAction(position, Carousel3DContext.SwipeAction.ERASE)
    }

    override fun onVerticalRoll(
        edgePosition: Int,
        direction: Carousel3DContext.RollingDirection
    ) {
        mCallback.onRequestListVerticalRoll(edgePosition, direction)
    }

    override fun areItemsSwipeable(): Boolean {
        return true
    }

    override fun getViewHolderForItemView(view: View): MateRequestViewHolder {
        return getRecyclerView()?.getChildViewHolder(view) as MateRequestViewHolder
    }
}