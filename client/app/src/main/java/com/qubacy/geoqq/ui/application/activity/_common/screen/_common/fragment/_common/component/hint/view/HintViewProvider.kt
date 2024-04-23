package com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment._common.component.hint.view

import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.qubacy.geoqq.databinding.ComponentHintBinding
import com.qubacy.geoqq.ui._common.util.view.extension.runAnimation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment._common.component._common.view.provider.ViewProvider
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.requests.MateRequestsFragment

open class HintViewProvider(
    parent: ViewGroup,
    attachToParent: Boolean = true
) : ViewProvider {
    companion object {
        const val DEFAULT_APPEARANCE_ANIMATION_DURATION = 300L
    }

    private lateinit var mBinding: ComponentHintBinding

    init {
        inflate(parent, attachToParent)
    }

    private fun inflate(parent: ViewGroup, attachToParent: Boolean) {
        val layoutInflater = LayoutInflater.from(parent.context)

        mBinding = ComponentHintBinding.inflate(layoutInflater, parent, attachToParent)
    }

    override fun getView(): View {
        return mBinding.root
    }

    fun setHintText(text: String) {
        mBinding.componentHintText.text = text
    }

    protected open fun animateAppearance(isAppearing: Boolean) {
        val hintView = getView()
        val hintViewHeight = hintView.measuredHeight

        hintView.runAnimation(
            DEFAULT_APPEARANCE_ANIMATION_DURATION,
            {
                it.alpha(if (isAppearing) 1f else 0f)
                it.translationY(if (isAppearing) 0f else -hintViewHeight.toFloat())
            },
            {
                hintView.alpha = if (isAppearing) 0f else 1f
                hintView.translationY = if (isAppearing) -hintViewHeight.toFloat() else 0f

                if (isAppearing) hintView.visibility = View.VISIBLE
            },
            {
                hintView.apply {
                    alpha = if (isAppearing) 1f else 0f
                    translationY = if (isAppearing) 0f else -hintViewHeight.toFloat()

                    if (!isAppearing) visibility = View.GONE
                }

                if (isAppearing)
                    scheduleAppearanceAnimation(
                        false,
                        MateRequestsFragment.HINT_TEXT_ANIMATION_DISAPPEARANCE_TIMEOUT
                    )
            }
        )
    }

    fun scheduleAppearanceAnimation(
        isAppearing: Boolean,
        duration: Long
    ) {
        object : CountDownTimer(duration, duration) {
            override fun onTick(millisUntilFinished: Long) {}
            override fun onFinish() { animateAppearance(isAppearing) }
        }.start()
    }
}