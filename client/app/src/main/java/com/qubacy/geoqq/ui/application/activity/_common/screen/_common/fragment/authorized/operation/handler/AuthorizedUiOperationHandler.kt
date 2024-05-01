package com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.authorized.operation.handler

import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.authorized.AuthorizedFragment
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.authorized.model.operation.LogoutUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.stateful.model.operation._common.UiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.stateful.operation.handler._common.UiOperationHandler

class AuthorizedUiOperationHandler(
    fragment: AuthorizedFragment
) : UiOperationHandler<AuthorizedFragment>(fragment) {
    override fun handleUiOperation(uiOperation: UiOperation): Boolean {
        if (uiOperation !is LogoutUiOperation) return false

        fragment.onAuthorizedFragmentLogout()

        return true
    }
}