package com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.authorized.model

import com.qubacy.geoqq.domain._common.usecase.authorized.result.error.ErrorWithLogoutDomainResult
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.authorized.model.operation.LogoutUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.stateful.model.operation._common.UiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.stateful.model.operation.error.ErrorUiOperation

interface AuthorizedViewModel {
    fun processErrorWithLogoutDomainResult(
        errorWithLogoutDomainResult: ErrorWithLogoutDomainResult
    ): List<UiOperation> {
        return listOf(
            ErrorUiOperation(errorWithLogoutDomainResult.error!!),
            LogoutUiOperation()
        )
    }
}