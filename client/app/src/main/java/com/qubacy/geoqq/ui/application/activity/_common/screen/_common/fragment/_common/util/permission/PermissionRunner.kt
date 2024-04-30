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

    private var mArePermissionsGranted: Boolean = false
    val arePermissionsGranted get() = mArePermissionsGranted

    fun requestPermissions(demandAll: Boolean = true) {
        if (fragment.getPermissionsToRequest() == null) return

        when {
            checkPermissions(demandAll) -> {
                processPermissionsGranted {
                    fragment.onPermissionsRequested()
                }
            }
            else -> {
                mIsRequestingPermissions = true
                mPermissionRequestLauncher = getPermissionRequestLauncher(demandAll) {
                    fragment.onPermissionsRequested()
                }

                mPermissionRequestLauncher.launch(fragment.getPermissionsToRequest())
            }
        }
    }

    private fun checkPermissions(demandAll: Boolean): Boolean {
        val permissionsToRequest = fragment.getPermissionsToRequest()!!
        var grantedPermissionCount = 0

        if (permissionsToRequest.isEmpty()) return true

        for (permission in permissionsToRequest) {
            if (ContextCompat.checkSelfPermission(fragment.requireContext(), permission)
                != PackageManager.PERMISSION_GRANTED
            ) {
                if (demandAll) return false
            }

            ++grantedPermissionCount
        }

        return grantedPermissionCount > 0
    }

    private fun getPermissionRequestLauncher(
        demandAll: Boolean,
        endAction: (() -> Unit)?
    ): ActivityResultLauncher<Array<String>> {
        return fragment.registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions(),
            getPermissionRequestCallback(demandAll, endAction)
        )
    }

    private fun getPermissionRequestCallback(
        demandAll: Boolean,
        endAction: (() -> Unit)?
    ): ActivityResultCallback<Map<String, Boolean>> {
        return ActivityResultCallback<Map<String, Boolean>> {
            val permissionsToRequest = fragment.getPermissionsToRequest()!!
            val deniedPermissions = mutableListOf<String>()

            for (requestedPermission in permissionsToRequest) {
                if (!it.containsKey(requestedPermission)) return@ActivityResultCallback

                if (it[requestedPermission] != true) {
                    deniedPermissions.add(requestedPermission)
                }
            }

            mIsRequestingPermissions = false

            if (demandAll) {
                if (deniedPermissions.isNotEmpty())
                    fragment.onRequestedPermissionsDenied(deniedPermissions)
                else
                    processPermissionsGranted(endAction)

            } else {
                if (deniedPermissions.size == permissionsToRequest.size)
                    fragment.onRequestedPermissionsDenied(deniedPermissions)
                else
                    processPermissionsGranted(endAction)
            }
        }
    }

    private fun processPermissionsGranted(endAction: (() -> Unit)?) {
        mArePermissionsGranted = true

        endAction?.invoke()

        fragment.onRequestedPermissionsGranted()
    }
}