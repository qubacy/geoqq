package com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.interlocutor.model

import com.qubacy.geoqq.domain.interlocutor.usecase._common.result.interlocutor.GetInterlocutorDomainResult
import com.qubacy.geoqq.domain.interlocutor.usecase._common.result.interlocutor.UpdateInterlocutorDomainResult
import com.qubacy.geoqq.domain.interlocutor.usecase._common.result.interlocutor._common.InterlocutorDomainResult
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.interlocutor.model.operation.ShowInterlocutorDetailsUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.interlocutor.model.operation.UpdateInterlocutorDetailsUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.business.model.BusinessViewModel
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.stateful.model.operation._common.UiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.presentation.user.UserPresentation

interface InterlocutorViewModel {
    fun onInterlocutorGetInterlocutor(domainResult: GetInterlocutorDomainResult): List<UiOperation> {
        val businessViewModel = getInterlocutorViewModelBusinessViewModel()

        if (!domainResult.isSuccessful()) return businessViewModel.onError(domainResult.error!!)

        val userPresentation = onInterlocutorInterlocutor(domainResult)

        return listOf(ShowInterlocutorDetailsUiOperation(userPresentation))
    }

    fun onInterlocutorUpdateInterlocutor(
        domainResult: UpdateInterlocutorDomainResult
    ): List<UiOperation> {
        val businessViewModel = getInterlocutorViewModelBusinessViewModel()

        if (!domainResult.isSuccessful()) return businessViewModel.onError(domainResult.error!!)

        val userPresentation = onInterlocutorInterlocutor(domainResult)

        return listOf(UpdateInterlocutorDetailsUiOperation(userPresentation))
    }

    fun onInterlocutorInterlocutor(domainResult: InterlocutorDomainResult): UserPresentation

    fun getInterlocutorViewModelBusinessViewModel(): BusinessViewModel<*, *>
}