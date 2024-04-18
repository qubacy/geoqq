package com.qubacy.geoqq.ui.application.activity._common.screen.mate.requests._common.presentation

import com.qubacy.geoqq.domain.mate.request.model.MateRequest
import com.qubacy.geoqq.ui.application.activity._common.screen._common.presentation.user.UserPresentation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.presentation.user.toUserPresentation

data class MateRequestPresentation(
    val id: Long,
    val user: UserPresentation
) {

}

fun MateRequest.toMateRequestPresentation(): MateRequestPresentation {
    return MateRequestPresentation(id, user.toUserPresentation())
}