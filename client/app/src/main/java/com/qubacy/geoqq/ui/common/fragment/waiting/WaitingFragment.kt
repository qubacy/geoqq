package com.qubacy.geoqq.ui.common.fragment.waiting

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.OnBackPressedDispatcher
import androidx.activity.addCallback
import androidx.annotation.LayoutRes
import com.qubacy.geoqq.R
import com.qubacy.geoqq.ui.common.fragment.common.BaseFragment
import com.qubacy.geoqq.ui.common.fragment.waiting.model.WaitingViewModel

abstract class WaitingFragment(
    @LayoutRes val loadingViewLayoutResId: Int = R.layout.component_loading_screen)
    : BaseFragment()
{
    abstract override val mModel: WaitingViewModel

    private var mLoadingView: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requireActivity().onBackPressedDispatcher.let {
            it.addCallback { onBackPressed(it,this) }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mModel.isWaiting.observe(viewLifecycleOwner) {
            if (it) handleWaitingStart()
            else handleWaitingStop()
        }
    }

    private fun onBackPressed(
        dispatcher: OnBackPressedDispatcher,
        callback: OnBackPressedCallback)
    {
        if (!mModel.isWaiting.value!!) {
            callback.remove()
            dispatcher.onBackPressed()

            return
        }

        handleWaitingAbort()
    }

    protected open fun handleWaitingStart() {
        if (mLoadingView == null) initLoadingView()

        // todo: it'd be better to play a provided animation when it's added to the container;

        (view as ViewGroup).addView(mLoadingView)
    }

    private fun initLoadingView() {
        mLoadingView = layoutInflater.inflate(
            loadingViewLayoutResId, view as ViewGroup, false)

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
//        if (!mModel.isWaiting.value!!) return

//        mModel.isWaiting.value = false

        if (mLoadingView == null) {
            // todo: what to do?

            return
        }

        // todo: it'd be better to play a provided animation before removing;

        (view as ViewGroup).removeView(mLoadingView)
    }
}