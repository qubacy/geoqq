package com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment._common.util.permission

interface PermissionRunnerCallback {
    fun getPermissionsToRequest(): Array<String>?

    fun onPermissionsRequested() {  }
    fun onRequestedPermissionsGranted(endAction: (() -> Unit)? = null) { }
    fun onRequestedPermissionsDenied(deniedPermissions: List<String>) { }
}