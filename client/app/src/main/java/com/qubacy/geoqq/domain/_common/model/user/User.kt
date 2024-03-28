package com.qubacy.geoqq.domain._common.model.user

import com.qubacy.geoqq.data.user.model.DataUser
import com.qubacy.geoqq.domain._common.model.image.Image
import com.qubacy.geoqq.domain._common.model.image.toImage

data class User(
    val id: Long,
    val username: String,
    val description: String?,
    val avatar: Image,
    val isMate: Boolean,
    val isDeleted: Boolean
) {

}

fun DataUser.toUser(): User {
    return User(id, username, description, avatar.toImage(), isMate, isDeleted)
}