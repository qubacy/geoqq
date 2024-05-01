package com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base._common.util.permission

interface PermissionRunnerCallback {
    fun getPermissionsToRequest(): Array<String>?

    fun onPermissionsRequested() {  }
    fun onRequestedPermissionsGranted() { }
    fun onRequestedPermissionsDenied(deniedPermissions: List<String>) { }
}