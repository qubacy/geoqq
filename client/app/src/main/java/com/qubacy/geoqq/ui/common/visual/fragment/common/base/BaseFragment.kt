package com.qubacy.geoqq.ui.common.visual.fragment.common.base

import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.qubacy.geoqq.common.error.common.Error
import com.qubacy.geoqq.ui.common.visual.component.dialog.error.ErrorDialog
import com.qubacy.geoqq.ui.common.visual.fragment.common.base.model.state.BaseUiState
import com.qubacy.geoqq.ui.common.visual.fragment.common.base.model.BaseViewModel
import com.qubacy.geoqq.ui.common.visual.fragment.common.styleable.StyleableFragment

abstract class BaseFragment() : StyleableFragment() {
    protected var mModel: BaseViewModel? = null

    private lateinit var mPermissionRequestLauncher: ActivityResultLauncher<Array<String>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initFlowContainerIfNull()

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

    override fun onDestroy() {
        clearFlowContainer()

        super.onDestroy()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initFlowContainerIfNull()
    }

    override fun onStart() {
        super.onStart()

        initFlowContainerIfNull()
    }

    override fun onStop() {
        super.onStop()
    }

    protected abstract fun initFlowContainerIfNull()
    protected abstract fun clearFlowContainer()

    protected fun checkUiStateForErrors(uiState: BaseUiState): Boolean {
        if (uiState.error == null) return false

        onErrorOccurred(uiState.error)

        return true
    }

    open fun onErrorOccurred(error: Error, callback: (() -> Unit)? = null) {
        Log.d("TEST", "onErrorOccurred")

        ErrorDialog.Builder(
            error.message,
            requireContext()
        ) { }
            .setOnDismissListener {
                handleError(error)
                callback?.invoke()

            }
            .create()
            .show()
    }

    open fun handleError(error: Error) {
        if (error.isCritical) {
            requireActivity().finishAndRemoveTask()
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

    fun closeSoftKeyboard() {
        val inputMethodManager =
            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager

        inputMethodManager?.hideSoftInputFromWindow(requireView().windowToken, 0)
    }
}