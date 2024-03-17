package com.qubacy.geoqq._common.exception._common

abstract class AppException(
    message: String? = null,
    cause: Throwable? = null
): Exception(message, cause) {

}