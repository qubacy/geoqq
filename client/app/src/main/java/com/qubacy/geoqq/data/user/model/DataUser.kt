package com.qubacy.geoqq.data.user.model

data class DataUser(
    val id: Long,
    val username: String,
    val description: String,
    val avatarId: Long,
    val isMate: Boolean
) {

}