package com.qubacy.geoqq.common.error

import androidx.annotation.StringRes

class Error(
    @StringRes val messageResId: Int,
    val level: Level = Level.NORMAL
) {
    enum class Level {
        NORMAL(), CRITICAL()
    }
}