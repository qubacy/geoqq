package com.qubacy.geoqq._common.error._test

import com.qubacy.geoqq._common.error.Error
import com.qubacy.geoqq._common.error.type.TestErrorType

object TestError {
    val normal = Error(
        TestErrorType.TEST.id, "normal fake error", false
    )
    val critical = Error(
        TestErrorType.TEST.id, "critical fake error", true
    )
}