package com.qubacy.geoqq.ui.application.activity._common.screen._common.presentation.user

import com.qubacy.geoqq.domain._common.model.user.User
import com.qubacy.geoqq.ui.application.activity._common.screen._common.presentation.image.ImagePresentation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.presentation.image.toImagePresentation

data class UserPresentation(
    val id: Long,
    val username: String,
    val description: String?,
    val avatar: ImagePresentation,
    val isMate: Boolean,
    val isDeleted: Boolean
) {

}

fun User.toUserPresentation(): UserPresentation {
    return UserPresentation(
        id,
        username,
        description,
        avatar.toImagePresentation(),
        isMate,
        isDeleted
    )
}