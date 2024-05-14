package com.qubacy.geoqq.ui.application.activity._common.screen._common.component.hint.view

import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.qubacy.geoqq.databinding.ComponentHintBinding
import com.qubacy.geoqq.ui._common.util.view.extension.runAnimation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.component._common.view.provider.ViewProvider

open class HintViewProvider(
    parent: ViewGroup,
    attachToParent: Boolean = true
) : ViewProvider {
    companion object {
        const val DEFAULT_APPEARANCE_ANIMATION_DURATION = 300L
        const val DEFAULT_HINT_TEXT_ANIMATION_DISAPPEARANCE_TIMEOUT = 3000L
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

    open fun animateAppearance(isAppearing: Boolean) {
        if (isAppearing == mBinding.root.isVisible) return

        val hintView = getView()

        if (hintView.measuredHeight == 0) hintView.measure(0, 0)

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
                        DEFAULT_HINT_TEXT_ANIMATION_DISAPPEARANCE_TIMEOUT
                    )
            }
        )
    }

    fun scheduleAppearanceAnimation(
        isAppearing: Boolean,
        duration: Long = DEFAULT_HINT_TEXT_ANIMATION_DISAPPEARANCE_TIMEOUT
    ) {
        if (isAppearing == mBinding.root.isVisible) return

        object : CountDownTimer(duration, duration) {
            override fun onTick(millisUntilFinished: Long) {}
            override fun onFinish() { animateAppearance(isAppearing) }
        }.start()
    }
}