package com.qubacy.geoqq.data.user.repository._common._test.context

import com.qubacy.geoqq.data.image.repository._common._test.mock.ImageDataRepositoryMockContainer
import com.qubacy.geoqq.data.user.model.DataUser
import com.qubacy.geoqq.data.user.repository._common.result.GetUsersByIdsDataResult
import com.qubacy.geoqq.data.user.repository._common.result.ResolveUsersDataResult
import com.qubacy.geoqq.data.user.repository._common.source.local.database._common.entity.UserEntity
import com.qubacy.geoqq.data.user.repository._common.source.remote.http.rest._common.api.response.GetUserResponse

object UserDataRepositoryTestContext {
    const val DEFAULT_LOCAL_USER_ID = 0L

    val DEFAULT_USER_ENTITY = UserEntity(
        DEFAULT_LOCAL_USER_ID,
        "local user", String(),
        0L, 0, 0
    )
    val DEFAULT_AVATAR = ImageDataRepositoryMockContainer.DEFAULT_DATA_IMAGE
        .copy(id = DEFAULT_USER_ENTITY.avatarId)
    val DEFAULT_DATA_USER = DataUser(
        DEFAULT_LOCAL_USER_ID,
        String(),
        String(),
        DEFAULT_AVATAR,
        false, false
    )

    val DEFAULT_USER_ID_USER_MAP = listOf(DEFAULT_DATA_USER).associateBy { it.id }.toMutableMap()

    val DEFAULT_GET_USERS_BY_IDS = GetUsersByIdsDataResult(true, listOf(DEFAULT_DATA_USER))
    val DEFAULT_RESOLVE_USERS = ResolveUsersDataResult(true, DEFAULT_USER_ID_USER_MAP)
    val DEFAULT_RESOLVE_USERS_WITH_LOCAL_USER = DEFAULT_RESOLVE_USERS

    val DEFAULT_GET_USER_RESPONSE = GetUserResponse(
        DEFAULT_LOCAL_USER_ID, "http user", "desc",
        0L, false, false
    )
}