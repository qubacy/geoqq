package com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment._common.util.permission

import android.content.pm.PackageManager
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

class PermissionRunner<FragmentType> (
    val fragment: FragmentType
) where FragmentType : Fragment, FragmentType : PermissionRunnerCallback {
    private lateinit var mPermissionRequestLauncher: ActivityResultLauncher<Array<String>>

    private var mIsRequestingPermissions: Boolean = false
    val isRequestingPermissions get() = mIsRequestingPermissions

    fun requestPermissions() {
        if (fragment.getPermissionsToRequest() == null) return

        when {
            checkPermissions() -> {
                fragment.onRequestedPermissionsGranted{ fragment.onPermissionsRequested() }
            }
            else -> {
                mIsRequestingPermissions = true
                mPermissionRequestLauncher = getPermissionRequestLauncher{
                    fragment.onPermissionsRequested()
                }

                mPermissionRequestLauncher.launch(fragment.getPermissionsToRequest())
            }
        }
    }

    private fun checkPermissions(): Boolean {
        for (permission in fragment.getPermissionsToRequest()!!) {
            if (ContextCompat.checkSelfPermission(fragment.requireContext(), permission)
                != PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }
        }

        return true
    }

    private fun getPermissionRequestLauncher(
        endAction: (() -> Unit)?
    ): ActivityResultLauncher<Array<String>> {
        return fragment.registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions(),
            getPermissionRequestCallback(endAction)
        )
    }

    private fun getPermissionRequestCallback(
        endAction: (() -> Unit)?
    ): ActivityResultCallback<Map<String, Boolean>> {
        return ActivityResultCallback<Map<String, Boolean>> {
            val deniedPermissions = mutableListOf<String>()

            for (requestedPermission in fragment.getPermissionsToRequest()!!) {
                if (!it.containsKey(requestedPermission)) return@ActivityResultCallback

                if (it[requestedPermission] != true) {
                    deniedPermissions.add(requestedPermission)
                }
            }

            mIsRequestingPermissions = false

            if (deniedPermissions.isEmpty()) fragment.onRequestedPermissionsGranted(endAction)
            else fragment.onRequestedPermissionsDenied(deniedPermissions)
        }
    }
}