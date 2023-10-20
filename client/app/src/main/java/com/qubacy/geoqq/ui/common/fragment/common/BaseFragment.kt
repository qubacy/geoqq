package com.qubacy.geoqq.ui.common.fragment.common

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.IntRange
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.snackbar.Snackbar
import com.qubacy.geoqq.R
import com.qubacy.geoqq.common.error.Error
import com.qubacy.geoqq.ui.common.activity.StyleableActivity
import com.qubacy.geoqq.ui.common.component.dialog.error.ErrorDialog
import com.qubacy.geoqq.ui.common.fragment.common.model.state.BaseUiState
import com.qubacy.geoqq.ui.common.fragment.common.model.BaseViewModel
import com.qubacy.geoqq.ui.model.MainViewModel
import com.qubacy.geoqq.ui.model.MainViewModelFactory

abstract class BaseFragment() : Fragment() {
    protected val mMainModel: MainViewModel by activityViewModels {
        MainViewModelFactory()
    }
    protected abstract val mModel: BaseViewModel

    private lateinit var mPermissionRequestLauncher: ActivityResultLauncher<Array<String>>

    @ColorInt
    private var mMessageSnackbarBackgroundColor: Int = 0
    @ColorInt
    private var mMessageSnackbarActionColor: Int = 0
    @ColorInt
    private var mMessageSnackbarTextColor: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (getPermissionsToRequest() == null) return

        when {
            checkPermissions() -> {
                onRequestedPermissionsGranted()
            }
            else -> {
                mPermissionRequestLauncher = getPermissionRequestLauncher()

                mPermissionRequestLauncher.launch(getPermissionsToRequest())
            }
        }
    }

    protected fun setTransitionWindowBackgroundColorResId(@ColorRes colorResId: Int) {
        val color = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
            resources.getColor(colorResId)
        else
            resources.getColor(colorResId, requireActivity().theme)

        requireActivity().window.setBackgroundDrawable(ColorDrawable(color))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val activity = requireActivity()

        if (activity is StyleableActivity)
            setCustomFragmentStyle(view, activity as StyleableActivity)
    }

    private fun setCustomFragmentStyle(view: View, styleableActivity: StyleableActivity) {
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

    private fun changeStatusBarColor(view: View, styleableActivity: StyleableActivity) {
        val attrs = intArrayOf(com.google.android.material.R.attr.statusBarBackground)
        val attrsTypedValues = view.context.theme.obtainStyledAttributes(attrs)

        styleableActivity.changeStatusBarColor(attrsTypedValues.getColor(0, 0))
    }

    protected fun checkUiStateForErrors(uiState: BaseUiState): Boolean {
        if (uiState.error == null) return false

        onErrorOccurred(uiState.error)

        return true
    }

    open fun onErrorOccurred(error: Error) {
        ErrorDialog.Builder(
            getString(
                error.messageResId),
            requireContext()) { handleError(error) }
            .create()
            .show()
    }

    open fun handleError(error: Error) {
        when (error.level) {
            Error.Level.NORMAL -> {}
            Error.Level.CRITICAL -> {
                requireActivity().finishAndRemoveTask()
            }
        }
    }

    private fun checkPermissions(): Boolean {
        for (permission in getPermissionsToRequest()!!) {
            if (ContextCompat.checkSelfPermission(requireContext(), permission)
                != PackageManager.PERMISSION_GRANTED)
            {
                return false
            }
        }

        return true
    }

    private fun getPermissionRequestLauncher(): ActivityResultLauncher<Array<String>> {
        return registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions(), getPermissionRequestCallback())
    }

    private fun getPermissionRequestCallback(): ActivityResultCallback<Map<String, Boolean>> {
        return ActivityResultCallback<Map<String, Boolean>> {
            val deniedPermissions = mutableListOf<String>()

            for (requestedPermission in getPermissionsToRequest()!!) {
                if (!it.containsKey(requestedPermission)) {
                    // cant be real!

                    return@ActivityResultCallback
                }

                if (it[requestedPermission] != true) {
                    deniedPermissions.add(requestedPermission)
                }
            }

            if (deniedPermissions.isEmpty()) onRequestedPermissionsGranted()
            else onRequestedPermissionsDenied(deniedPermissions)
        }
    }

    open fun getPermissionsToRequest(): Array<String>? {
        return null
    }

    open fun onRequestedPermissionsGranted() {

    }

    open fun onRequestedPermissionsDenied(deniedPermissions: List<String>) {

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