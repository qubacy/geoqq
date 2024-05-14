package com.qubacy.geoqq.data.user.repository.source.local.database._common._test.context

import com.qubacy.geoqq.data.user.repository._common.source.local.database._common.entity.UserEntity

object LocalUserDatabaseDataSourceTestContext {
    val DEFAULT_USER_ENTITY = UserEntity(
        0L,
        "test username",
        "test aboutme",
        0L,
        0,
        0
    )
}