package com.qubacy.geoqq.ui.application.activity._common.screen._common._test.context

import com.qubacy.geoqq.ui.application.activity._common.screen._common.presentation.image.ImagePresentation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.presentation.user.UserPresentation

object ScreenTestContext {
    fun generateUserPresentation(
        imagePresentation: ImagePresentation,
        id: Long = 0L
    ): UserPresentation {
        return UserPresentation(
            id, "test username", String(),
            imagePresentation, false, false
        )
    }
}