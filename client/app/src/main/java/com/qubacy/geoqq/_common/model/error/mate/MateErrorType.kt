package com.qubacy.geoqq._common.model.error.mate

import com.qubacy.geoqq._common.model.error._common.domain.ErrorDomain
import com.qubacy.geoqq._common.model.error._common.type.ErrorType

enum class MateErrorType (
    override val id: Long,
    override val domain: ErrorDomain = ErrorDomain.MATE
) : ErrorType {
    MATE_REQUEST_ALREADY_SENT(2),
    MATE_REQUEST_ALREADY_GOTTEN(3),
    MATE_REQUEST_NOT_FOUND(4),
    MATE_REQUEST_NOT_AVAILABLE(5),
    ALREADY_MATES(7),
    USER_ALREADY_DELETED(8),
    USER_NOT_FOUND(9),
    MATE_CHAT_NOT_FOUND(10),
    MATE_CHAT_NOT_AVAILABLE(11);
}