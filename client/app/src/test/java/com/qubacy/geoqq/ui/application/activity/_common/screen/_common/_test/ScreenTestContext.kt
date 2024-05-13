package com.qubacy.geoqq.ui.application.activity._common.screen._common._test

import com.qubacy.geoqq.domain._common._test.context.UseCaseTestContext
import com.qubacy.geoqq.ui.application.activity._common.screen._common.presentation.image.toImagePresentation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.presentation.user.toUserPresentation

object ScreenTestContext {
    val DEFAULT_USER_PRESENTATION = UseCaseTestContext.DEFAULT_USER.toUserPresentation()
    val DEFAULT_IMAGE_PRESENTATION = UseCaseTestContext.DEFAULT_IMAGE.toImagePresentation()
}