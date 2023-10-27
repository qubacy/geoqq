package com.qubacy.geoqq.common.error.common

abstract class ErrorBase(
    val level: Level = Level.NORMAL
) {
    enum class Level(val id: Int) {
        NORMAL(0), CRITICAL(1);
    }
}