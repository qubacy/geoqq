package com.qubacy.geoqq.ui.application.activity._common.screen.mate.chat.error.type

import com.qubacy.geoqq._common.model.error._common.domain.ErrorDomain
import com.qubacy.geoqq._common.model.error._common.type.ErrorType

enum class UiMateChatErrorType(
    override val id: Long,
    override val domain: ErrorDomain = ErrorDomain.UI_MATE_CHAT
) : ErrorType {
    INVALID_MESSAGE(0);
}