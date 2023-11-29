package com.qubacy.geoqq.data.common.util.generator

import com.qubacy.geoqq.data.user.model.DataUser

object DataUserGeneratorUtility {
    const val DEFAULT_AVATAR_ID = 0L

    fun generateDataUsers(count: Int, startId: Long = 0L, areMates: Boolean = true): List<DataUser> {
        return LongRange(startId, startId + count - 1).map {
            DataUser(it, "test $it", "desc", DEFAULT_AVATAR_ID, areMates)
        }
    }
}