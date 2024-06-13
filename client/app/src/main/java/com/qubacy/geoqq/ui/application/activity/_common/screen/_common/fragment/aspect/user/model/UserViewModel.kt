package com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.user.model

import com.qubacy.geoqq.domain.user.usecase._common.result.get.GetUserDomainResult
import com.qubacy.geoqq.domain._common.usecase.aspect.user.result.update.UserUpdatedDomainResult
import com.qubacy.geoqq.domain._common.usecase.aspect.user.result._common.UserDomainResult
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.business.model.BusinessViewModel
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.stateful.model.operation._common.UiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.presentation.user.UserPresentation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.presentation.user.toUserPresentation

interface UserViewModel {
    fun onUserGetUser(domainResult: GetUserDomainResult): List<UiOperation> {
        val businessViewModel = getUserViewModelBusinessViewModel()

        if (!domainResult.isSuccessful()) return businessViewModel.onError(domainResult.error!!)

        val userPresentation = onUserUser(domainResult)

        return generateUserGetUserUiOperations(userPresentation)
    }

    fun generateUserGetUserUiOperations(userPresentation: UserPresentation): List<UiOperation> {
        return emptyList()
    }

    fun onUserUpdateUser(domainResult: UserUpdatedDomainResult): List<UiOperation> {
        val businessViewModel = getUserViewModelBusinessViewModel()

        if (!domainResult.isSuccessful()) return businessViewModel.onError(domainResult.error!!)

        val userPresentation = onUserUser(domainResult)

        return generateUserUpdateUserUiOperations(userPresentation)
    }

    fun generateUserUpdateUserUiOperations(userPresentation: UserPresentation): List<UiOperation> {
        return emptyList()
    }

    fun onUserUser(domainResult: UserDomainResult): UserPresentation {
        return domainResult.interlocutor!!.toUserPresentation()
    }

    fun getUserViewModelBusinessViewModel(): BusinessViewModel<*, *>
}