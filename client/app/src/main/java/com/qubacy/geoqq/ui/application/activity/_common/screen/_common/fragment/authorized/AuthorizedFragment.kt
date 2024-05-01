package com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.authorized

import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.authorized.model.operation.LogoutUiOperation

interface AuthorizedFragment {
    fun processLogoutOperation(logoutUiOperation: LogoutUiOperation) {
        navigateToLogin()
    }

    fun navigateToLogin()
}