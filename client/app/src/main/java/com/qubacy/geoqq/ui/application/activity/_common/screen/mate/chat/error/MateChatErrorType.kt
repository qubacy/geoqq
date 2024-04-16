package com.qubacy.geoqq.ui.application.activity._common.screen.mate.chat.error

import com.qubacy.geoqq._common.model.error.domain.ErrorDomain
import com.qubacy.geoqq._common.model.error.type.ErrorType

enum class MateChatErrorType(
    override val id: Long,
    override val domain: ErrorDomain = ErrorDomain.UI_MATE_CHAT
) : ErrorType {
    INVALID_MESSAGE(0);
}