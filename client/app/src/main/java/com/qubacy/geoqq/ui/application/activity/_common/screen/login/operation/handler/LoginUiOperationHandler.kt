package com.qubacy.geoqq.ui.application.activity._common.screen.login.operation.handler

import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.stateful.model.operation._common.UiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.stateful.operation.handler._common.UiOperationHandler
import com.qubacy.geoqq.ui.application.activity._common.screen.login.LoginFragment
import com.qubacy.geoqq.ui.application.activity._common.screen.login.model.operation.SignInUiOperation

class LoginUiOperationHandler(
    fragment: LoginFragment
) : UiOperationHandler<LoginFragment>(fragment) {
    override fun handleUiOperation(uiOperation: UiOperation): Boolean {
        if (uiOperation !is SignInUiOperation) return false

        fragment.onLoginFragmentSignIn()

        return true
    }
}