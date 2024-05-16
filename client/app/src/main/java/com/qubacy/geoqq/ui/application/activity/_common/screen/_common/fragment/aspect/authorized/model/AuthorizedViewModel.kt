package com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.authorized.model

import com.qubacy.geoqq.domain._common.usecase.aspect.authorized.result.error.ErrorWithLogoutDomainResult
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.authorized.model.operation.LogoutUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.stateful.model.operation._common.UiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.stateful.model.operation.error.ErrorUiOperation

interface AuthorizedViewModel {
    fun onAuthorizedErrorWithLogout(
        errorWithLogoutDomainResult: ErrorWithLogoutDomainResult
    ): List<UiOperation> {
        return listOf(
            ErrorUiOperation(errorWithLogoutDomainResult.error!!),
            LogoutUiOperation()
        )
    }
}