package com.qubacy.geoqq.ui.application.activity._common.screen.mate._common._test.context

import com.qubacy.geoqq.domain.mate._common._test.context.MateUseCaseTestContext
import com.qubacy.geoqq.ui.application.activity._common.screen.mate._common.presentation.toMateChatPresentation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate._common.presentation.toMateMessagePresentation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.requests._common.presentation.toMateRequestPresentation

object MateViewModelTestContext {
    val DEFAULT_MATE_MESSAGE_PRESENTATION = MateUseCaseTestContext.DEFAULT_MATE_MESSAGE
        .toMateMessagePresentation()
    val DEFAULT_MATE_CHAT_PRESENTATION = MateUseCaseTestContext.DEFAULT_MATE_CHAT
        .toMateChatPresentation()
    val DEFAULT_MATE_REQUEST_PRESENTATION = MateUseCaseTestContext.DEFAULT_MATE_REQUEST
        .toMateRequestPresentation()
}