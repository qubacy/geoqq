package com.qubacy.geoqq._common.error.type

import com.qubacy.geoqq._common.model.error._common.domain.ErrorDomain
import com.qubacy.geoqq._common.model.error._common.type.ErrorType

enum class TestErrorType(
    override val id: Long,
    override val domain: ErrorDomain = ErrorDomain.UI_LOGIN
) : ErrorType {
    TEST(0);
}