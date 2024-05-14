package com.qubacy.geoqq.ui.application.activity._common.screen.myprofile.model._common._test.context

import com.qubacy.geoqq.domain.myprofile.usecase._common._test.context.MyProfileUseCaseTestContext
import com.qubacy.geoqq.ui.application.activity._common.screen.myprofile._common.presentation.toMyProfilePresentation

object MyProfileTestContext {
    val DEFAULT_MY_PROFILE_PRESENTATION = MyProfileUseCaseTestContext.DEFAULT_MY_PROFILE
        .toMyProfilePresentation()
}