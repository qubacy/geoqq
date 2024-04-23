package com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment._common.component.placeholder

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import com.qubacy.geoqq.databinding.ComponentSurfacePlaceholderBinding
import com.qubacy.geoqq.ui._common.util.view.extension.runVisibilityAnimation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment._common.component._common.view.provider.ViewProvider

open class SurfacePlaceholderViewProvider(
    parent: ViewGroup,
    attachToParent: Boolean = true
) : ViewProvider {
    companion object {
        const val DEFAULT_VISIBILITY_ANIMATION_DURATION = 200L
    }

    private lateinit var mBinding: ComponentSurfacePlaceholderBinding

    private var mImage: Drawable? = null

    init {
        inflate(parent, attachToParent)
    }

    private fun inflate(parent: ViewGroup, attachToParent: Boolean) {
        val layoutInflater = LayoutInflater.from(parent.context)

        mBinding = ComponentSurfacePlaceholderBinding.inflate(
            layoutInflater, parent, attachToParent)
    }

    override fun getView(): View {
        return mBinding.root
    }

    fun setContent(
        image: Drawable? = null,
        text: String? = null
    ) {
        image?.let { setImage(it) }
        text?.let {
            mBinding.componentSurfacePlaceholderText.text = text
        }
    }

    fun setText(text: String) {
        mBinding.componentSurfacePlaceholderText.text = text
    }

    fun setAnimatedVectorImage(
        @DrawableRes imageResId: Int
    ) {
        val image = AnimatedVectorDrawableCompat.create(mBinding.root.context, imageResId)!!

        setImage(image)
    }

    private fun setImage(image: Drawable) {
        mImage = image

        mBinding.componentSurfacePlaceholderImage.setImageDrawable(mImage)
    }

    fun getImage(): Drawable? {
        return mImage
    }

    open fun setIsVisible(
        isVisible: Boolean
    ) {
        if (isVisible) {
            mBinding.root.runVisibilityAnimation(true, DEFAULT_VISIBILITY_ANIMATION_DURATION)

            val image = mImage

            if (image is AnimatedVectorDrawableCompat) image.start()

        } else {
            mBinding.root.runVisibilityAnimation(false, DEFAULT_VISIBILITY_ANIMATION_DURATION)
        }
    }
}