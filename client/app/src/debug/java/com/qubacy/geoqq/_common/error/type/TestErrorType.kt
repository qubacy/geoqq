package com.qubacy.geoqq._common.error.type

import com.qubacy.geoqq._common.error.domain.ErrorDomain

enum class TestErrorType(
    override val id: Long,
    override val domain: ErrorDomain = ErrorDomain.UI
) : ErrorType {
    TEST(0);
}