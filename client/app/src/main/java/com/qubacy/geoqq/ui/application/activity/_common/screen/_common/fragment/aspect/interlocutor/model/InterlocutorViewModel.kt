package com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.interlocutor.model

import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.interlocutor.model.operation.ShowInterlocutorDetailsUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.interlocutor.model.operation.UpdateInterlocutorDetailsUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.user.model.UserViewModel
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.stateful.model.operation._common.UiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.presentation.user.UserPresentation

interface InterlocutorViewModel : UserViewModel {
    override fun generateUserGetUserUiOperations(
        userPresentation: UserPresentation
    ): List<UiOperation> {
        return listOf(ShowInterlocutorDetailsUiOperation(userPresentation))
    }

    override fun generateUserUpdateUserUiOperations(
        userPresentation: UserPresentation
    ): List<UiOperation> {
        return listOf(UpdateInterlocutorDetailsUiOperation(userPresentation))
    }
}