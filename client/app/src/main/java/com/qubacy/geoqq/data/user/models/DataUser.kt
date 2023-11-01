package com.qubacy.geoqq.data.user.models

data class DataUser(
    val username: String,
    val description: String,
    val avatarBase64: String,
    val isMate: Boolean
) {

}