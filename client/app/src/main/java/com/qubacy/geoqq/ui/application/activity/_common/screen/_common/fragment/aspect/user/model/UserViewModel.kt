package com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.user.model

import com.qubacy.geoqq.domain.user.usecase._common.result.get.GetUserDomainResult
import com.qubacy.geoqq.domain.user.usecase._common.result.update.UpdateUserDomainResult
import com.qubacy.geoqq.domain.user.usecase._common.result._common.UserDomainResult
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.interlocutor.model.operation.ShowInterlocutorDetailsUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.interlocutor.model.operation.UpdateInterlocutorDetailsUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.business.model.BusinessViewModel
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.stateful.model.operation._common.UiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.presentation.user.UserPresentation

interface UserViewModel {
    fun onUserGetUser(domainResult: GetUserDomainResult): List<UiOperation> {
        val businessViewModel = getUserViewModelBusinessViewModel()

        if (!domainResult.isSuccessful()) return businessViewModel.onError(domainResult.error!!)

        val userPresentation = onUserUser(domainResult)

        return generateUserGetUserUiOperations(userPresentation)
    }

    fun generateUserGetUserUiOperations(userPresentation: UserPresentation): List<UiOperation>

    fun onUserUpdateUser(domainResult: UpdateUserDomainResult): List<UiOperation> {
        val businessViewModel = getUserViewModelBusinessViewModel()

        if (!domainResult.isSuccessful()) return businessViewModel.onError(domainResult.error!!)

        val userPresentation = onUserUser(domainResult)

        return generateUserGetUserUiOperations(userPresentation)
    }

    fun generateUserUpdateUserUiOperations(userPresentation: UserPresentation): List<UiOperation>

    fun onUserUser(domainResult: UserDomainResult): UserPresentation

    fun getUserViewModelBusinessViewModel(): BusinessViewModel<*, *>
}