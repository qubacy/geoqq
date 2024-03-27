package com.qubacy.geoqq.domain._common.model.user

import com.qubacy.geoqq.domain._common.model.image.Image

data class User(
    val id: Long,
    val username: String,
    val description: String?,
    val avatar: Image,
    val isMate: Boolean,
    val isDeleted: Boolean
) {

}