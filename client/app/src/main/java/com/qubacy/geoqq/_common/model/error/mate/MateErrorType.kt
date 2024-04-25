package com.qubacy.geoqq._common.model.error.mate

import com.qubacy.geoqq._common.model.error._common.domain.ErrorDomain
import com.qubacy.geoqq._common.model.error._common.type.ErrorType

enum class MateErrorType (
    override val id: Long,
    override val domain: ErrorDomain = ErrorDomain.MATE
) : ErrorType {
    ERROR_2(2),
    ERROR_3(3),
    ERROR_4(4),
    ERROR_5(5),
    ERROR_7(7),
    ERROR_8(8),
    ERROR_9(9);
}