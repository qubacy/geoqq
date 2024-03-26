package com.qubacy.geoqq.data.user.model

import com.qubacy.geoqq.data.user.repository.source.http.response.GetUserResponse
import com.qubacy.geoqq.data.user.repository.source.local.entity.UserEntity

data class DataUser(
    val id: Long,
    val username: String,
    val description: String?,
    val avatarId: Long,
    val isMate: Boolean,
    val isDeleted: Boolean
) {

}

fun DataUser.toUserEntity(): UserEntity {
    // todo: is it ok?:
    val isMateFlag = if (isMate) 1 else 0
    val isDeletedFlag = if (isDeleted) 1 else 0

    return UserEntity(id, username, description, avatarId, isMateFlag, isDeletedFlag)
}

fun UserEntity.toDataUser(): DataUser {
    return DataUser(id, username, description, avatarId, isMate == 1, isDeleted == 1)
}

fun GetUserResponse.toDataUser(): DataUser {
    return DataUser(id, username, description, avatarId, isMate, isDeleted)
}