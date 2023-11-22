package com.qubacy.geoqq.ui.screen.common

import android.net.Uri
import com.qubacy.geoqq.domain.common.model.User

object ScreenContext {
    val DEFAULT_URI: Uri = Uri.parse(String())

    fun generateTestUsers(count: Int, areMates: Boolean): List<User> {
        return IntRange(0, count - 1).map {
            User(it.toLong(), "test $it", "desc", DEFAULT_URI, areMates)
        }
    }
}