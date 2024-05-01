package com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.authorized

import androidx.annotation.CallSuper

interface AuthorizedFragment {
    @CallSuper
    fun onAuthorizedFragmentLogout() {
        navigateToLogin()
    }

    fun navigateToLogin()
}