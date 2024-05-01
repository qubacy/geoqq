package com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.popup

import android.view.View
import android.widget.Toast
import androidx.annotation.StringRes
import com.google.android.material.snackbar.Snackbar
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment._common.BaseFragment

interface PopupFragment {
    var messageSnackbar: Snackbar?

    fun getPopupAnchorView(): View

    fun getPopupFragmentBaseFragment(): BaseFragment<*>

    open fun onPopupMessageOccurred(
        message: String,
        duration: Int = Toast.LENGTH_SHORT
    ) {
        val fragment = getPopupFragmentBaseFragment()

        messageSnackbar = Snackbar.make(
            fragment.requireContext(), fragment.requireView(), message, duration
        ).setAnchorView(getPopupAnchorView())

        messageSnackbar!!.show()
    }

    fun onPopupMessageOccurred(
        @StringRes message: Int,
        duration: Int = Toast.LENGTH_SHORT
    ) {
        val fragment = getPopupFragmentBaseFragment()

        onPopupMessageOccurred(fragment.getString(message), duration)
    }

    fun closePopupMessage() {
        messageSnackbar?.dismiss()
    }
}