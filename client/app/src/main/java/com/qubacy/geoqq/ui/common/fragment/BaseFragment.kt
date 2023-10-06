package com.qubacy.geoqq.ui.common.fragment

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.IntRange
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.qubacy.geoqq.R
import com.qubacy.geoqq.common.error.Error
import com.qubacy.geoqq.ui.common.component.dialog.error.ErrorDialog
import com.qubacy.geoqq.ui.common.fragment.model.BaseViewModel

abstract class BaseFragment() : Fragment() {
    protected abstract val mModel: BaseViewModel

    private lateinit var mPermissionRequestLauncher: ActivityResultLauncher<Array<String>>

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mModel.error.observe(viewLifecycleOwner) {
            if (it == null) return@observe

            onErrorOccurred(it)
        }
    }

    open fun onErrorOccurred(error: Error) {
        ErrorDialog.Builder(
            getString(
                error.messageResId),
            requireContext()) { handleError(error) }
            .create()
            .show()
    }

    abstract fun handleError(error: Error)

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
        )

        messageSnackbar.setAction(
            R.string.fragment_base_show_message_action_dismiss_text
        ) {
            messageSnackbar.dismiss()
        }.setDuration(displayDuration).show()
    }
}