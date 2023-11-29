package com.qubacy.geoqq.domain.common.util.generator

import android.net.Uri
import com.qubacy.geoqq.domain.common.model.User

object UserGeneratorUtility {
    val DEFAULT_URI: Uri = Uri.parse(String())

    fun generateUsers(count: Int, startId: Long = 0L, areMates: Boolean = true): List<User> {
        return LongRange(startId, startId + count - 1).map {
            User(it, "test $it", "desc", DEFAULT_URI, areMates)
        }
    }
}