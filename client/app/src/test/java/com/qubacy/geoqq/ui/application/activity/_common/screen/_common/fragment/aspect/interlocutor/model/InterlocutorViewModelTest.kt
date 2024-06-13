package com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.interlocutor.model

import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.interlocutor.model.operation.ShowInterlocutorDetailsUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.interlocutor.model.operation.UpdateInterlocutorDetailsUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.user.model.UserViewModelTest
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.business.model.BusinessViewModel
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.stateful.model.operation._common.UiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.presentation.user.UserPresentation
import org.junit.Assert

interface InterlocutorViewModelTest<
    ViewModelType : BusinessViewModel<*, *>
> : UserViewModelTest<ViewModelType> {
    override fun assertUserGetUserOperations(
        userPresentation: UserPresentation,
        uiOperations: List<UiOperation>
    ) {
        if (uiOperations.isEmpty()) return

        val showInterlocutorUiOperation = uiOperations.first()

        Assert.assertEquals(
            ShowInterlocutorDetailsUiOperation::class,
            showInterlocutorUiOperation::class
        )
        Assert.assertEquals(
            userPresentation,
            (showInterlocutorUiOperation as ShowInterlocutorDetailsUiOperation).interlocutor
        )
    }

    override fun assertUserUpdateUserOperations(
        userPresentation: UserPresentation,
        uiOperations: List<UiOperation>
    ) {
        if (uiOperations.isEmpty()) return

        val updateInterlocutorOperation = uiOperations.first()

        Assert.assertEquals(
            UpdateInterlocutorDetailsUiOperation::class,
            updateInterlocutorOperation::class
        )
        Assert.assertEquals(
            userPresentation,
            (updateInterlocutorOperation as UpdateInterlocutorDetailsUiOperation).interlocutor
        )
    }
}