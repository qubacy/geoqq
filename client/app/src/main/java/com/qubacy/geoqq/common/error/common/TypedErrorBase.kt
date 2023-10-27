package com.qubacy.geoqq.common.error.common

abstract class TypedErrorBase(
    val errorType: ErrorTypeEnum,
    level: Level
) : ErrorBase(level) {
    enum class ErrorTypeEnum {
        LOCAL(), REMOTE();
    }

}