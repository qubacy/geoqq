package com.qubacy.geoqq._common.exception.error

import com.qubacy.geoqq._common.model.error.Error
import com.qubacy.geoqq._common.exception._common.AppException

class ErrorAppException(
    val error: Error
) : AppException() {

}