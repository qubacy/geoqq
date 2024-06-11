package com.qubacy.geoqq._common.exception.error

import com.qubacy.geoqq._common.model.error._common.Error
import com.qubacy.geoqq._common.exception._common.AppException
import kotlin.reflect.full.isSubclassOf

class ErrorAppException(
    val error: Error
) : AppException() {
    override fun equals(other: Any?): Boolean {
        if (other == null || !other::class.isSubclassOf(ErrorAppException::class))
            return false

        other as ErrorAppException

        return (error == other.error)
    }

    override fun hashCode(): Int {
        return error.hashCode()
    }
}