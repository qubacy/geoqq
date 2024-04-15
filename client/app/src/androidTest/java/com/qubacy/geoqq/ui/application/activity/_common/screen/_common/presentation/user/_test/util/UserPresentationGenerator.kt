package com.qubacy.geoqq.ui.application.activity._common.screen._common.presentation.user._test.util

import com.qubacy.geoqq.ui.application.activity._common.screen._common.presentation.image.ImagePresentation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.presentation.user.UserPresentation

object UserPresentationGenerator {
    fun generateUserPresentation(
        chatId: Long,
        imagePresentation: ImagePresentation
    ): UserPresentation {
        val id = chatId + 1

        return UserPresentation(
            id, "test $id", String(),
            imagePresentation, false, false
        )
    }
}