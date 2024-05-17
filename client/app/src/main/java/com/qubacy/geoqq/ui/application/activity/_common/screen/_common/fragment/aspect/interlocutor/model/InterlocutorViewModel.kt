package com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.interlocutor.model

import com.qubacy.geoqq.domain.user.usecase._common.result.get.GetUserDomainResult
import com.qubacy.geoqq.domain.user.usecase._common.result.update.UpdateUserDomainResult
import com.qubacy.geoqq.domain.user.usecase._common.result._common.UserDomainResult
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.interlocutor.model.operation.ShowInterlocutorDetailsUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.interlocutor.model.operation.UpdateInterlocutorDetailsUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.business.model.BusinessViewModel
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.stateful.model.operation._common.UiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.presentation.user.UserPresentation

interface InterlocutorViewModel {
    fun onInterlocutorGetInterlocutor(domainResult: GetUserDomainResult): List<UiOperation> {
        val businessViewModel = getInterlocutorViewModelBusinessViewModel()

        if (!domainResult.isSuccessful()) return businessViewModel.onError(domainResult.error!!)

        val userPresentation = onInterlocutorInterlocutor(domainResult)

        return listOf(ShowInterlocutorDetailsUiOperation(userPresentation))
    }

    fun onInterlocutorUpdateInterlocutor(
        domainResult: UpdateUserDomainResult
    ): List<UiOperation> {
        val businessViewModel = getInterlocutorViewModelBusinessViewModel()

        if (!domainResult.isSuccessful()) return businessViewModel.onError(domainResult.error!!)

        val userPresentation = onInterlocutorInterlocutor(domainResult)

        return listOf(UpdateInterlocutorDetailsUiOperation(userPresentation))
    }

    fun onInterlocutorInterlocutor(domainResult: UserDomainResult): UserPresentation

    fun getInterlocutorViewModelBusinessViewModel(): BusinessViewModel<*, *>
}