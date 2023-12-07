package com.qubacy.geoqq.ui.common.visual.fragment.waiting

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.OnBackPressedDispatcher
import androidx.activity.addCallback
import androidx.annotation.LayoutRes
import com.qubacy.geoqq.R
import com.qubacy.geoqq.ui.common.visual.fragment.common.base.BaseFragment
import com.qubacy.geoqq.ui.common.visual.fragment.waiting.model.WaitingViewModel

abstract class WaitingFragment(
    @LayoutRes val loadingViewLayoutResId: Int = R.layout.component_loading_screen
) : BaseFragment() {
    private var mLoadingView: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requireActivity().onBackPressedDispatcher.let {
            it.addCallback { onBackPressed(it,this) }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if ((mModel as WaitingViewModel).isWaiting.value == true) {
            handleWaitingStart()
        }
        (mModel as WaitingViewModel).isWaiting.observe(viewLifecycleOwner) {
            if (it) handleWaitingStart()
            else handleWaitingStop()
        }
    }

    private fun onBackPressed(
        dispatcher: OnBackPressedDispatcher,
        callback: OnBackPressedCallback)
    {
        if (!(mModel as WaitingViewModel).isWaiting.value!!) {
            callback.remove()
            dispatcher.onBackPressed()

            return
        }

        handleWaitingAbort()
    }

    protected open fun handleWaitingStart() {
        if (mLoadingView == null) initLoadingView()

        // todo: it'd be better to play a provided animation when it's added to the container;

        if (mLoadingView!!.parent == null)
            (view as ViewGroup).addView(mLoadingView)
    }

    private fun initLoadingView() {
        mLoadingView = LayoutInflater.from(requireView().context)
            .inflate(loadingViewLayoutResId, view as ViewGroup, false)

        mLoadingView?.apply {
            isClickable = true
            isFocusable = true

            setOnClickListener { handleWaitingAbort() }
        }
    }

    protected open fun handleWaitingAbort() {
        handleWaitingStop()
    }

    protected open fun handleWaitingStop() {
        if (mLoadingView == null) {
            // todo: what to do?

            return
        }

        // todo: it'd be better to play a provided animation before removing;

        (view as ViewGroup).removeView(mLoadingView)
    }
}