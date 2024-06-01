package com.qubacy.geoqq.data.user.model

import com.qubacy.geoqq.data.image.model.DataImage
import com.qubacy.geoqq.data.user.repository._common.source.remote.http.rest._common.api.response.GetUserResponse
import com.qubacy.geoqq.data.user.repository._common.source.local.database._common.entity.UserEntity
import com.qubacy.geoqq.data.user.repository._common.source.remote.http.websocket._common.event.payload.updated.UserUpdatedEventPayload

data class DataUser(
    val id: Long,
    val username: String,
    val description: String?,
    val avatar: DataImage,
    val isMate: Boolean,
    val isDeleted: Boolean
) {

}

fun DataUser.toUserEntity(): UserEntity {
    // todo: is it ok?:
    val isMateFlag = if (isMate) 1 else 0
    val isDeletedFlag = if (isDeleted) 1 else 0

    return UserEntity(id, username, description, avatar.id, isMateFlag, isDeletedFlag)
}

fun UserEntity.toDataUser(avatar: DataImage): DataUser {
    return DataUser(id, username, description, avatar, isMate == 1, isDeleted == 1)
}

fun GetUserResponse.toDataUser(avatar: DataImage): DataUser {
    return DataUser(id, username, description, avatar, isMate, isDeleted)
}

fun UserUpdatedEventPayload.toDataUser(avatar: DataImage): DataUser {
    return DataUser(id, username, description, avatar, isMate, isDeleted)
}