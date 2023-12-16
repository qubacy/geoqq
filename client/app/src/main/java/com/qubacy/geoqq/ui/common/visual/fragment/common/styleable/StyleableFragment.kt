package com.qubacy.geoqq.ui.common.visual.fragment.common.styleable

import android.annotation.SuppressLint
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.IntRange
import androidx.annotation.StringRes
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.qubacy.geoqq.R

abstract class StyleableFragment : Fragment() {
    @ColorInt
    private var mMessageSnackbarBackgroundColor: Int = 0
    @ColorInt
    private var mMessageSnackbarActionColor: Int = 0
    @ColorInt
    private var mMessageSnackbarTextColor: Int = 0

    protected fun getColorIntById(@ColorRes colorResId: Int): Int {
        return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
            resources.getColor(colorResId)
        else
            resources.getColor(colorResId, requireActivity().theme)
    }

    protected fun setTransitionWindowBackgroundColorResId(@ColorRes colorResId: Int) {
        val color = getColorIntById(colorResId)

        requireActivity().window.setBackgroundDrawable(ColorDrawable(color))
    }

    protected fun setTransitionWindowBackgroundDrawableResId(@DrawableRes drawableResId: Int) {
        val drawable = ResourcesCompat.getDrawable(
            resources, drawableResId, requireActivity().theme)

        requireActivity().window.setBackgroundDrawable(drawable)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val activity = requireActivity()

        if (activity is com.qubacy.geoqq.ui.common.visual.activity.StyleableActivity)
            setCustomFragmentStyle(view, activity as com.qubacy.geoqq.ui.common.visual.activity.StyleableActivity)
    }

    private fun setCustomFragmentStyle(view: View, styleableActivity: com.qubacy.geoqq.ui.common.visual.activity.StyleableActivity) {
        changeStatusBarColor(view, styleableActivity)
        initSnackbarColors(view)
    }

    @SuppressLint("ResourceType")
    private fun initSnackbarColors(view: View) {
        val attrs = intArrayOf(
            com.google.android.material.R.attr.colorSecondaryContainer,
            com.google.android.material.R.attr.colorOnSecondaryContainer,
        )
        val attrsTypedValues = view.context.theme.obtainStyledAttributes(attrs)

        mMessageSnackbarBackgroundColor = attrsTypedValues.getColor(0, 0)
        mMessageSnackbarTextColor = attrsTypedValues.getColor(1, 0)
        mMessageSnackbarActionColor = attrsTypedValues.getColor(1, 0)
    }

    private fun changeStatusBarColor(view: View, styleableActivity: com.qubacy.geoqq.ui.common.visual.activity.StyleableActivity) {
        val attrs = intArrayOf(com.google.android.material.R.attr.statusBarBackground)
        val attrsTypedValues = view.context.theme.obtainStyledAttributes(attrs)

        styleableActivity.changeStatusBarColor(attrsTypedValues.getColor(0, 0))
    }

    open fun showMessage(
        @StringRes messageResId: Int,
        @IntRange(from = -2) displayDuration: Int = Snackbar.LENGTH_LONG
    ) {
        val messageSnackbar = Snackbar.make(
            requireContext(),
            requireView(),
            getString(messageResId),
            displayDuration
        ).apply {
            setBackgroundTint(mMessageSnackbarBackgroundColor)
            setTextColor(mMessageSnackbarTextColor)
            setActionTextColor(mMessageSnackbarActionColor)
        }

        messageSnackbar.setAction(
            R.string.fragment_base_show_message_action_dismiss_text
        ) {
            messageSnackbar.dismiss()
        }.setDuration(displayDuration).show()
    }
}